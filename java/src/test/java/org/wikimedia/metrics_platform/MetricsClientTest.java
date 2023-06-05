package org.wikimedia.metrics_platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.wikimedia.metrics_platform.config.StreamConfigFetcher.METRICS_PLATFORM_SCHEMA_TITLE;
import static org.wikimedia.metrics_platform.config.StreamConfigFixtures.streamConfig;
import static org.wikimedia.metrics_platform.curation.CurationFilterFixtures.curationFilter;
import static org.wikimedia.metrics_platform.event.EventProcessed.fromEvent;

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
import org.wikimedia.metrics_platform.context.ClientData;
import org.wikimedia.metrics_platform.context.ClientDataFixtures;
import org.wikimedia.metrics_platform.context.PageDataFixtures;
import org.wikimedia.metrics_platform.event.Event;
import org.wikimedia.metrics_platform.event.EventProcessed;

@ExtendWith(MockitoExtension.class)
class MetricsClientTest {

    @Mock private ClientData clientData;
    @Mock private SessionController mockSessionController;
    @Mock private SamplingController mockSamplingController;
    private MetricsClient client;
    private BlockingQueue<EventProcessed> eventQueue;
    private AtomicReference<SourceConfig> sourceConfig;

    @BeforeEach void createEventProcessorMetricsClient() {
        eventQueue = new LinkedBlockingQueue<>(10);
        sourceConfig = new AtomicReference<>(SourceConfigFixtures.getTestSourceConfig());
        clientData = ClientDataFixtures.getTestClientData();

        client = MetricsClient.builder(clientData)
                .sessionController(mockSessionController)
                .samplingController(mockSamplingController)
                .sourceConfigRef(sourceConfig)
                .eventQueue(eventQueue)
                .build();
    }

    @Test void testSubmit() {
        Event event = new Event(METRICS_PLATFORM_SCHEMA_TITLE, "test_stream", "test_event");
        client.submit(event);
        EventProcessed eventProcessed = eventQueue.peek();
        String stream = eventProcessed.getStream();
        assertThat(stream).isEqualTo("test_stream");
    }

    @Test void testSubmitMetricsEventWithoutPageData() {
        when(mockSamplingController.isInSample(streamConfig(curationFilter()))).thenReturn(true);

        Map<String, Object> customDataMap = getTestCustomData();
        client.submitMetricsEvent("test_event", customDataMap);

        assertThat(eventQueue).isNotEmpty();

        EventProcessed queuedEvent = eventQueue.remove();

        // Verify custom data
        assertThat(queuedEvent.getName()).isEqualTo("test_event");
        Map<String, Object> customData = queuedEvent.getCustomData();
        Map<String, String> isEditor = (Map<String, String>) customData.get("is_editor");
        assertThat(isEditor.get("data_type")).isEqualTo("boolean");
        assertThat(isEditor.get("value")).isEqualTo("true");
        Map<String, String> action = (Map<String, String>) customData.get("action");
        assertThat(action.get("data_type")).isEqualTo("string");
        assertThat(action.get("value")).isEqualTo("click");
        Map<String, String> screenSize = (Map<String, String>) customData.get("screen_size");
        assertThat(screenSize.get("data_type")).isEqualTo("number");
        assertThat(screenSize.get("value")).isEqualTo("1080");

        // Verify that page data is not included
        assertThat(queuedEvent.getPageData().getId()).isNull();
        assertThat(queuedEvent.getPageData().getTitle()).isNull();
        assertThat(queuedEvent.getPageData().getNamespace()).isNull();
        assertThat(queuedEvent.getPageData().getNamespaceName()).isNull();
        assertThat(queuedEvent.getPageData().getRevisionId()).isNull();
        assertThat(queuedEvent.getPageData().getWikidataItemQid()).isNull();
        assertThat(queuedEvent.getPageData().getContentLanguage()).isNull();
    }

    @Test void testSubmitMetricsEventWithPageData() {
        when(mockSamplingController.isInSample(streamConfig(curationFilter()))).thenReturn(true);

        client.submitMetricsEvent("test_event", PageDataFixtures.getTestPageData(), getTestCustomData());

        assertThat(eventQueue).isNotEmpty();

        EventProcessed queuedEvent = eventQueue.remove();

        assertThat(queuedEvent.getPageData().getId()).isEqualTo(1);
        assertThat(queuedEvent.getPageData().getTitle()).isEqualTo("Test Page Title");
        assertThat(queuedEvent.getPageData().getNamespace()).isEqualTo(0);
        assertThat(queuedEvent.getPageData().getNamespaceName()).isEqualTo("");
        assertThat(queuedEvent.getPageData().getRevisionId()).isEqualTo(1);
        assertThat(queuedEvent.getPageData().getWikidataItemQid()).isEqualTo("Q1");
        assertThat(queuedEvent.getPageData().getContentLanguage()).isEqualTo("zh");
    }

    @Test void testSubmitWhenEventQueueIsFull() {
        for (int i = 1; i <= 10; i++) {
            Event event = new Event("test_schema" + i, "test_stream" + i, "test_event" + i);
            EventProcessed eventProcessed = fromEvent(event);
            eventQueue.add(eventProcessed);
        }
        EventProcessed oldestEvent = eventQueue.peek();

        Event event11 = new Event("test_schema11", "test_stream11", "test_event11");
        EventProcessed eventProcessed11 = fromEvent(event11);
        client.submit(eventProcessed11);

        assertThat(eventQueue).doesNotContain(oldestEvent);

        Boolean containsNewestEvent = eventQueue.stream().anyMatch(event -> event.getName().equals("test_event11"));
        assertThat(containsNewestEvent).isTrue();
    }

    @Test void testTouchSessionOnAppPause() {
        when(mockSamplingController.isInSample(streamConfig(curationFilter()))).thenReturn(true);
        fillEventQueue();
        assertThat(eventQueue).isNotEmpty();

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
        when(mockSamplingController.isInSample(streamConfig(curationFilter()))).thenReturn(true);
        fillEventQueue();
        assertThat(eventQueue).isNotEmpty();

        client.onAppClose();
        verify(mockSessionController).closeSession();
    }

    @Test void testAddRequiredMetadata() {
        Event event = new Event("test/event/1.0.0", "test_event", "testEvent");
        assertThat(event.getTimestamp()).isNull();

        client.submit(event);
        EventProcessed queuedEvent = eventQueue.remove();

        assertThat(queuedEvent.getTimestamp()).isNotNull();
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

    private void fillEventQueue() {
        for (int i = 1; i <= 10; i++) {
            client.submitMetricsEvent("test_event", getTestCustomData());
        }
    }
}
