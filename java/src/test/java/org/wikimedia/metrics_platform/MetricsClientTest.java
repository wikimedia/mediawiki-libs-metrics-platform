package org.wikimedia.metrics_platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.wikimedia.metrics_platform.config.StreamConfigFetcher.METRICS_PLATFORM_SCHEMA_TITLE;
import static org.wikimedia.metrics_platform.config.StreamConfigFixtures.streamConfig;
import static org.wikimedia.metrics_platform.curation.CurationFilterFixtures.curationFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wikimedia.metrics_platform.config.SourceConfig;
import org.wikimedia.metrics_platform.config.SourceConfigFixtures;
import org.wikimedia.metrics_platform.event.Event;

@ExtendWith(MockitoExtension.class)
class MetricsClientTest {

    @Mock private ClientMetadata mockClientMetadata;
    @Mock private SessionController mockSessionController;
    @Mock private SamplingController mockSamplingController;
    private MetricsClient client;
    private BlockingQueue<Event> eventQueue;
    private AtomicReference<SourceConfig> sourceConfig;

    @BeforeEach void createEventProcessorMetricsClient() {
        eventQueue = new LinkedBlockingQueue<>(10);
        sourceConfig = new AtomicReference<>(SourceConfigFixtures.getTestSourceConfig());

        client = MetricsClient.builder(mockClientMetadata)
                .sessionController(mockSessionController)
                .samplingController(mockSamplingController)
                .sourceConfigRef(sourceConfig)
                .eventQueue(eventQueue)
                .build();
    }

    @Test void testSubmit() {
        Event event = new Event(METRICS_PLATFORM_SCHEMA_TITLE, "test_stream", "test_event");
        client.submit(event);

        assertThat(eventQueue).contains(event);
    }

    @Test void testSubmitMetricsEvent() throws InterruptedException {
        when(mockSamplingController.isInSample(streamConfig(curationFilter()))).thenReturn(true);

        Map<String, Object> customDataMap = getTestCustomData();
        client.submitMetricsEvent("test_event", customDataMap);

        assertThat(eventQueue).isNotEmpty();

        Event take = eventQueue.take();

        assertThat(take.getName()).isEqualTo("test_event");
        Map<String, Object> customData = take.getCustomData();
        Map<String, String> isEditor = (Map<String, String>) customData.get("is_editor");
        assertThat(isEditor.get("data_type")).isEqualTo("boolean");
        assertThat(isEditor.get("value")).isEqualTo("true");
        Map<String, String> action = (Map<String, String>) customData.get("action");
        assertThat(action.get("data_type")).isEqualTo("string");
        assertThat(action.get("value")).isEqualTo("click");
        Map<String, String> screenSize = (Map<String, String>) customData.get("screen_size");
        assertThat(screenSize.get("data_type")).isEqualTo("number");
        assertThat(screenSize.get("value")).isEqualTo("1080");
    }

    @Test void testSubmitWhenEventQueueIsFull() {
        for (int i = 1; i <= 10; i++) {
            Event event = new Event("test_schema" + i, "test_stream" + i, "test_event" + i);
            eventQueue.add(event);
        }
        Event event11 = new Event("schema", "stream", "event");
        client.submitMetricsEvent(event11.getName(), getTestCustomData());

        assertThat(eventQueue).doesNotContain(event11);
    }

    @Test void testTouchSessionOnAppPause() {
        client.onAppPause();
        verify(mockSessionController).touchSession();
    }

    @Test void testResumeSessionOnAppResume() {
        client.onAppResume();
        verify(mockSessionController).touchSession();
    }

    @Test void testResetSession() {
        client.resetSession();
        verify(mockSessionController).beginSession();
    }

    @Test void testCloseSessionOnAppClose() {
        client.onAppClose();
        verify(mockSessionController).closeSession();
    }

    @Test void testAddRequiredMetadata() {
        Event event = new Event("test/event/1.0.0", "test_event", "testEvent");
        client.submit(event);

        verify(mockClientMetadata).getAgentAppInstallId();
        verify(mockSessionController).getSessionId();
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
