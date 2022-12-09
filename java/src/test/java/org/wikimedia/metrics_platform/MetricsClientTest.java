package org.wikimedia.metrics_platform;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.wikimedia.metrics_platform.config.StreamConfigFetcher.METRICS_PLATFORM_SCHEMA_TITLE;
import static org.wikimedia.metrics_platform.context.ContextValue.IS_PRODUCTION;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_TITLE;
import static org.wikimedia.metrics_platform.context.ContextValue.PLATFORM;
import static org.wikimedia.metrics_platform.context.ContextValue.PLATFORM_FAMILY;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wikimedia.metrics_platform.config.SampleConfig;
import org.wikimedia.metrics_platform.config.SourceConfig;
import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.context.CustomData;
import org.wikimedia.metrics_platform.curation.CurationFilter;

import com.google.gson.Gson;

@ExtendWith(MockitoExtension.class)
public class MetricsClientTest {

    @Mock private ClientMetadata mockClientMetadata;
    @Mock private SessionController mockSessionController;
    @Mock private SamplingController mockSamplingController;
    private MetricsClient client;
    private static CurationFilter curationFilter;
    private BlockingQueue<Event> eventQueue;
    private AtomicReference<SourceConfig> sourceConfig;

    @BeforeAll
    static void setUp() {
        Gson gson = new Gson();
        String curationFilterJson = "{\"page_id\":{\"less_than\":500,\"not_equals\":42},\"page_namespace_text\":" +
            "{\"equals\":\"Talk\"},\"user_is_logged_in\":{\"equals\":true},\"user_edit_count_bucket\":" +
            "{\"in\":[\"100-999 edits\",\"1000+ edits\"]},\"user_groups\":{\"contains_all\":" +
            "[\"user\",\"autoconfirmed\"],\"does_not_contain\":\"sysop\"},\"device_pixel_ratio\":" +
            "{\"greater_than_or_equals\":1.5,\"less_than_or_equals\":2.5}}";
        curationFilter = gson.fromJson(curationFilterJson, CurationFilter.class);
    }

    @BeforeEach
    public void createEventProcessorMetricsClient() {
        eventQueue = new LinkedBlockingQueue<>(10);
        sourceConfig = new AtomicReference<>(getTestSourceConfig());

        client =  new MetricsClient(
                mockClientMetadata,
                mockSessionController,
                mockSamplingController,
                sourceConfig,
                eventQueue
        );
    }

    @Test
    public void testSubmit() {
        Event event = new Event(METRICS_PLATFORM_SCHEMA_TITLE, "test_stream", "test_event");
        client.submit(event);

        assertThat(eventQueue).contains(event);
    }

    @Test
    public void testDispatch() throws InterruptedException {
        when(mockSamplingController.isInSample(getTestStreamConfig())).thenReturn(true);

        Map<String, Object> customDataMap = getTestCustomData();
        client.dispatch("test_event", customDataMap);

        assertThat(eventQueue).isNotEmpty();

        Event take = eventQueue.take();

        assertThat(take.getName()).isEqualTo("test_event");
        assertThat(take.getCustomData()).containsExactlyInAnyOrder(
                CustomData.of("is_editor", TRUE),
                CustomData.of("action", "click"),
                CustomData.of("screen_size", 1080)
        );
    }

    @Test
    public void testSubmitWhenEventQueueIsFull() {
        for (int i = 1; i <= 10; i++) {
            Event event = new Event("test_schema" + i, "test_stream" + i, "test_event" + i);
            eventQueue.add(event);
        }
        Event event11 = new Event("schema", "stream", "event");
        client.dispatch(event11.getName(), getTestCustomData());

        assertThat(eventQueue).doesNotContain(event11);
    }

    @Test
    public void testTouchSessionOnApplicationPause() {
        client.onApplicationPause();
        verify(mockSessionController).touchSession();
    }

    @Test
    public void testResumeSessionOnApplicationResume() {
        client.onApplicationResume();
        verify(mockSessionController).touchSession();
    }

    @Test
    public void testAddRequiredMetadata() {
        Event event = new Event("test/event/1.0.0", "test_event", "testEvent");
        client.submit(event);

        verify(mockClientMetadata).getAppInstallId();
        verify(mockSessionController).getSessionId();
    }

    /**
     * Convenience method for getting stream config.
     */
    private static StreamConfig getTestStreamConfig() {
        String[] provideValues = {
            PLATFORM,
            PLATFORM_FAMILY,
            PAGE_TITLE,
            IS_PRODUCTION
        };
        Set<String> events = Collections.singleton("test_event");
        SampleConfig sampleConfig = new SampleConfig(1.0f, SampleConfig.Identifier.UNIT, "pageview");

        return new StreamConfig(
            "test_stream",
            METRICS_PLATFORM_SCHEMA_TITLE,
            DestinationEventService.LOCAL,
            new StreamConfig.ProducerConfig(
                new StreamConfig.MetricsPlatformClientConfig(
                    events,
                    Arrays.asList(provideValues),
                    curationFilter
                )
            ),
            sampleConfig
        );
    }

    /**
     * Convenience method for getting a stream config map.
     */
    public static Map<String, StreamConfig> getTestStreamConfigMap() {
        StreamConfig streamConfig = getTestStreamConfig();
        return singletonMap(streamConfig.getStreamName(), streamConfig);
    }

    /**
     * Convenience method for getting source config.
     */
    public static SourceConfig getTestSourceConfig() {
        return new SourceConfig(getTestStreamConfigMap());
    }

    /**
     * Convenience method for adding test custom data.
     */
    private Map<String, Object> getTestCustomData() {
        Map<String, Object> customData = new HashMap<>();

        customData.put("action", "click");
        customData.put("is_editor", true);
        customData.put("screen_size", 1080);

        return customData;
    }
}
