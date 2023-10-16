package org.wikimedia.metrics_platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.stream;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.wikimedia.metrics_platform.config.StreamConfigFetcher.METRICS_PLATFORM_SCHEMA_TITLE;
import static org.wikimedia.metrics_platform.config.StreamConfigFixtures.streamConfig;
import static org.wikimedia.metrics_platform.curation.CurationFilterFixtures.curationFilter;
import static org.wikimedia.metrics_platform.event.EventProcessed.fromEvent;

import java.util.Collections;
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
import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.context.ClientData;
import org.wikimedia.metrics_platform.context.DataFixtures;
import org.wikimedia.metrics_platform.context.CustomData;
import org.wikimedia.metrics_platform.context.CustomDataType;
import org.wikimedia.metrics_platform.context.PageData;
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
        clientData = DataFixtures.getTestClientData();

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

    @Test void testSubmitMetricsEventWithoutClientData() {
        when(mockSamplingController.isInSample(streamConfig(curationFilter()))).thenReturn(true);

        Map<String, Object> customDataMap = getTestCustomData();
        client.submitMetricsEvent("test_event", customDataMap);

        assertThat(eventQueue).isNotEmpty();

        EventProcessed queuedEvent = eventQueue.remove();

        // Verify custom data
        assertThat(queuedEvent.getName()).isEqualTo("test_event");
        Map<String, CustomData> customData = queuedEvent.getCustomData();
        CustomData isEditor = customData.get("is_editor");
        assertThat(isEditor.getType()).isEqualTo(CustomDataType.BOOLEAN);
        assertThat(isEditor.getValue()).isEqualTo("true");
        CustomData action = customData.get("action");
        assertThat(action.getType()).isEqualTo(CustomDataType.STRING);
        assertThat(action.getValue()).isEqualTo("click");
        CustomData screenSize = customData.get("screen_size");
        assertThat(screenSize.getType()).isEqualTo(CustomDataType.NUMBER);
        assertThat(screenSize.getValue()).isEqualTo("1080");

        // Verify that client data is not included
        assertThat(queuedEvent.getAgentData().getClientPlatform()).isNull();
        assertThat(queuedEvent.getPageData().getId()).isNull();
        assertThat(queuedEvent.getPageData().getTitle()).isNull();
        assertThat(queuedEvent.getPageData().getNamespace()).isNull();
        assertThat(queuedEvent.getPageData().getNamespaceName()).isNull();
        assertThat(queuedEvent.getPageData().getRevisionId()).isNull();
        assertThat(queuedEvent.getPageData().getWikidataItemQid()).isNull();
        assertThat(queuedEvent.getPageData().getContentLanguage()).isNull();
        assertThat(queuedEvent.getMediawikiData().getSkin()).isNull();
        assertThat(queuedEvent.getPerformerData().getId()).isNull();
    }

    @Test void testSubmitMetricsEventWithClientData() {
        when(mockSamplingController.isInSample(streamConfig(curationFilter()))).thenReturn(true);

        // Update a few client data members to confirm that the client data parameter during metrics client
        // instantiation gets overridden with the client data sent with the event.
        ClientData clientData = DataFixtures.getTestClientData();
        PageData pageData = PageData.builder()
                .id(108)
                .title("Revised Page Title")
                .namespace(0)
                .namespaceName("Main")
                .revisionId(1L)
                .wikidataItemQid("Q123456")
                .contentLanguage("en")
                .isRedirect(false)
                .groupsAllowedToMove(Collections.singleton("*"))
                .groupsAllowedToEdit(Collections.singleton("*"))
                .build();
        clientData.setPageData(pageData);

        client.submitMetricsEvent("test_event", clientData, getTestCustomData());

        assertThat(eventQueue).isNotEmpty();

        EventProcessed queuedEvent = eventQueue.remove();

        // Verify custom data
        assertThat(queuedEvent.getName()).isEqualTo("test_event");
        Map<String, CustomData> customData = queuedEvent.getCustomData();
        CustomData isEditor = customData.get("is_editor");
        assertThat(isEditor.getType()).isEqualTo(CustomDataType.BOOLEAN);
        assertThat(isEditor.getValue()).isEqualTo("true");
        CustomData action = customData.get("action");
        assertThat(action.getType()).isEqualTo(CustomDataType.STRING);
        assertThat(action.getValue()).isEqualTo("click");
        CustomData screenSize = customData.get("screen_size");
        assertThat(screenSize.getType()).isEqualTo(CustomDataType.NUMBER);
        assertThat(screenSize.getValue()).isEqualTo("1080");

        // Verify client data
        assertThat(queuedEvent.getAgentData().getAppInstallId()).isEqualTo("ffffffff-ffff-ffff-ffff-ffffffffffff");
        assertThat(queuedEvent.getAgentData().getClientPlatform()).isEqualTo("android");
        assertThat(queuedEvent.getAgentData().getClientPlatformFamily()).isEqualTo("app");

        assertThat(queuedEvent.getPageData().getId()).isEqualTo(108);
        assertThat(queuedEvent.getPageData().getTitle()).isEqualTo("Revised Page Title");
        assertThat(queuedEvent.getPageData().getNamespace()).isEqualTo(0);
        assertThat(queuedEvent.getPageData().getNamespaceName()).isEqualTo("Main");
        assertThat(queuedEvent.getPageData().getRevisionId()).isEqualTo(1L);
        assertThat(queuedEvent.getPageData().getWikidataItemQid()).isEqualTo("Q123456");
        assertThat(queuedEvent.getPageData().getContentLanguage()).isEqualTo("en");

        assertThat(queuedEvent.getMediawikiData().getSkin()).isEqualTo("vector");
        assertThat(queuedEvent.getMediawikiData().getVersion()).isEqualTo("1.40.0-wmf.20");
        assertThat(queuedEvent.getMediawikiData().getIsProduction()).isTrue();
        assertThat(queuedEvent.getMediawikiData().getIsDebugMode()).isFalse();
        assertThat(queuedEvent.getMediawikiData().getDatabase()).isEqualTo("enwiki");
        assertThat(queuedEvent.getMediawikiData().getSiteContentLanguage()).isEqualTo("en");
        assertThat(queuedEvent.getMediawikiData().getSiteContentLanguageVariant()).isEqualTo("en-zh");

        assertThat(queuedEvent.getPerformerData().getId()).isEqualTo(1);
        assertThat(queuedEvent.getPerformerData().getName()).isEqualTo("TestPerformer");
        assertThat(queuedEvent.getPerformerData().getIsLoggedIn()).isTrue();
        assertThat(queuedEvent.getPerformerData().getPageviewId()).isEqualTo("eeeeeeeeeeeeeeeeeeee");
        assertThat(queuedEvent.getPerformerData().getGroups()).contains("*");
        assertThat(queuedEvent.getPerformerData().getIsBot()).isFalse();
        assertThat(queuedEvent.getPerformerData().getLanguage()).isEqualTo("zh");
        assertThat(queuedEvent.getPerformerData().getLanguageVariant()).isEqualTo("zh-tw");
        assertThat(queuedEvent.getPerformerData().getCanProbablyEditPage()).isTrue();
        assertThat(queuedEvent.getPerformerData().getEditCount()).isEqualTo(10);
        assertThat(queuedEvent.getPerformerData().getEditCountBucket()).isEqualTo("5-99 edits");
        assertThat(queuedEvent.getPerformerData().getRegistrationDt()).isEqualTo("2023-03-01T01:08:30Z");
    }

    @Test void testSubmitMetricsEventIncludesSample() {
        StreamConfig streamConfig = streamConfig(curationFilter());

        when(mockSamplingController.isInSample(streamConfig)).thenReturn(true);

        Map<String, Object> customDataMap = getTestCustomData();
        client.submitMetricsEvent("test_event", customDataMap);

        assertThat(eventQueue).isNotEmpty();

        EventProcessed queuedEvent = eventQueue.remove();

        assertThat(queuedEvent.getSample()).isEqualTo(streamConfig.getSampleConfig());
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
