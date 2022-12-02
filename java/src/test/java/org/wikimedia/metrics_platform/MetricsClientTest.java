package org.wikimedia.metrics_platform;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.config.SampleConfig;
import org.wikimedia.metrics_platform.config.SourceConfig;
import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.config.StreamConfigFetcher;
import org.wikimedia.metrics_platform.context.ContextController;
import org.wikimedia.metrics_platform.curation.CurationController;
import org.wikimedia.metrics_platform.curation.CurationFilter;

import com.google.gson.Gson;

public class MetricsClientTest {

    private final ClientMetadata mockClientMetadata = mock(ClientMetadata.class);
    private final EventSender mockEventSender = mock(EventSender.class);
    private final SessionController mockSessionController = mock(SessionController.class);
    private final SamplingController mockSamplingController = mock(SamplingController.class);
    private final ContextController mockContextController = mock(ContextController.class);
    private final CurationController mockCurationController = new AlwaysAcceptCurationController();
    private MetricsClient client;
    private static CurationFilter curationFilter;

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
    public void resetClient() {
        reset(mockClientMetadata, mockSessionController, mockSamplingController, mockContextController);
    }

    @BeforeEach
    public void createMetricsClient() {
        client =  new MetricsClient(
                mockClientMetadata,
                mockEventSender,
                mockSessionController,
                mockSamplingController,
                mockContextController,
                mockCurationController,
                null, 10
        );
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
    public void testAddEventMetadata() throws IOException {
        when(mockSamplingController.isInSample(getTestStreamConfig())).thenReturn(true);

        Event event = new Event("test/event/1.0.0", "test_event", "testEvent");
        client.submit(event);

        verify(mockClientMetadata).getAppInstallId();
        verify(mockSessionController).getSessionId();
    }

    @Test
    public void testDispatchEvent() {
        client.setSourceConfig(getTestSourceConfig());
        when(mockSamplingController.isInSample(getTestStreamConfig())).thenReturn(true);

        Map<String, Object> customDataMap = getTestCustomData();
        client.dispatch("test_event", customDataMap);

        assertThat(client.hasPendingEvents()).isTrue();

        client.sendEnqueuedEvents();
        assertThat(client.hasPendingEvents()).isFalse();
    }

    @Test
    public void eventsNotSentWhenFetchStreamConfigFails() throws Exception {
        client.setSourceConfig(null);
        client.submit(new Event("stream", "stream", "event"));
        verify(mockEventSender, never()).sendEvents(anyString(), anyCollection());
    }

    @Test
    public void testEventSubmissionTaskSendsEnqueuedEvents() throws Exception {
        client.setSourceConfig(getTestSourceConfig());

        Event event = new Event("test_schema", "test_stream", "test_event");
        client.submit(event);
        client.sendEnqueuedEvents();

        verify(mockEventSender, times(1)).sendEvents(anyString(), anyCollection());
    }

    @Test
    public void testEventsRemovedFromOutputBufferOnSuccess() throws IOException {
        client.setSourceConfig(getTestSourceConfig());

        Event event = new Event("test_schema", "test_stream", "test_event");
        client.submit(event);
        client.sendEnqueuedEvents();

        assertThat(client.hasPendingEvents()).isFalse();
    }

    @Test
    public void testEventsRemainInOutputBufferOnFailure() throws IOException {
        Event event = new Event("fake_schema", "fake_stream", "fake_event");
        client.submit(event);

        assertThat(client.hasPendingEvents()).isTrue();
    }

    private static class AlwaysAcceptCurationController extends CurationController {
        @Override
        public boolean eventPassesCurationRules(Event event, StreamConfig streamConfig) {
            return true;
        }
    }

    /**
     * Convenience method for getting stream config.
     */
    private static StreamConfig getTestStreamConfig() {
        String[] provideValues = {
            "agent_client_platform",
            "agent_client_platform_family",
            "mediawiki_database",
            "mediawiki_is_production"
        };
        Set<String> events = new HashSet<>();
        events.add("test_event");
        SampleConfig sampleConfig = new SampleConfig(1.0f, SampleConfig.Identifier.UNIT, "pageview");

        return new StreamConfig(
            "test_stream",
            StreamConfigFetcher.METRICS_PLATFORM_SCHEMA_TITLE,
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
    private static Map<String, StreamConfig> getTestStreamConfigMap() {
        StreamConfig streamConfig = getTestStreamConfig();
        return singletonMap(streamConfig.getStreamName(), streamConfig);
    }

    /**
     * Convenience method for getting source config.
     */
    private static SourceConfig getTestSourceConfig() {
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
