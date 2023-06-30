package org.wikimedia.metrics_platform;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.wikimedia.metrics_platform.config.StreamConfigFixtures.streamConfig;
import static org.wikimedia.metrics_platform.event.EventFixtures.minimalEventProcessed;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wikimedia.metrics_platform.config.CurationFilter;
import org.wikimedia.metrics_platform.config.SourceConfig;
import org.wikimedia.metrics_platform.config.SourceConfigFixtures;
import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.context.DataFixtures;
import org.wikimedia.metrics_platform.event.EventProcessed;

@ExtendWith(MockitoExtension.class)
class EventProcessorTest {
    @Mock private EventSender mockEventSender;
    @Mock private CurationFilter curationFilter;
    private final AtomicReference<SourceConfig> sourceConfig = new AtomicReference<>();
    private final BlockingQueue<EventProcessed> eventQueue = new LinkedBlockingQueue<>(10);
    private EventProcessor eventProcessor;

    @BeforeEach void clearEventQueue() {
        eventQueue.clear();
    }

    @BeforeEach void createEventProcessor() {
        sourceConfig.set(SourceConfigFixtures.getTestSourceConfig(curationFilter));

        eventProcessor = new EventProcessor(
                new ContextController(DataFixtures.getTestClientData()),
                sourceConfig,
                mockEventSender,
                eventQueue,
                false
        );
    }

    @Test void enqueuedEventsAreSent() throws IOException {
        whenEventsArePassingCurationFilter();

        eventQueue.offer(minimalEventProcessed());
        eventProcessor.sendEnqueuedEvents();
        verify(mockEventSender).sendEvents(any(URL.class), anyCollection());
    }

    @Test void eventsNotPassingCurationFiltersAreDropped() throws IOException {
        whenEventsAreNotPassingCurationFilter();

        eventQueue.offer(minimalEventProcessed());
        eventProcessor.sendEnqueuedEvents();

        verify(mockEventSender, never()).sendEvents(any(URL.class), anyCollection());
        assertThat(eventQueue).isEmpty();
    }

    @Test void eventsAreRemovedFromQueueOnceSent() {
        whenEventsArePassingCurationFilter();

        eventQueue.offer(minimalEventProcessed());
        eventProcessor.sendEnqueuedEvents();

        assertThat(eventQueue).isEmpty();
    }

    @Test void eventsRemainInOutputBufferOnFailure() throws IOException {
        whenEventsArePassingCurationFilter();
        doThrow(IOException.class).when(mockEventSender).sendEvents(any(URL.class), anyCollection());

        eventQueue.offer(minimalEventProcessed());
        eventProcessor.sendEnqueuedEvents();

        assertThat(eventQueue).isNotEmpty();
    }

    @Test void eventsAreEnrichedBeforeBeingSent() throws IOException {
        whenEventsArePassingCurationFilter();

        eventQueue.offer(minimalEventProcessed());
        eventProcessor.sendEnqueuedEvents();

        ArgumentCaptor<Collection<EventProcessed>> eventCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(mockEventSender).sendEvents(any(URL.class), eventCaptor.capture());

        EventProcessed sentEvent = eventCaptor.getValue().iterator().next();

        // Verify client data based on provided values in StreamConfigFixtures.
        assertThat(sentEvent.getAgentData().getClientPlatform()).isEqualTo("android");
        assertThat(sentEvent.getAgentData().getClientPlatformFamily()).isEqualTo("app");
        assertThat(sentEvent.getPageData().getTitle()).isEqualTo("Test Page Title");
        assertThat(sentEvent.getMediawikiData().getDatabase()).isEqualTo("enwiki");
        assertThat(sentEvent.getPerformerData().getSessionId()).isEqualTo("eeeeeeeeeeeeeeeeeeee");
    }

    @Test void eventsNotSentWhenFetchStreamConfigFails() {
        sourceConfig.set(null);

        eventQueue.offer(minimalEventProcessed());
        eventProcessor.sendEnqueuedEvents();

        assertThat(eventQueue).isNotEmpty();
    }

    private void whenEventsArePassingCurationFilter() {
        when(curationFilter.apply(any(EventProcessed.class))).thenReturn(TRUE);
    }

    private void whenEventsAreNotPassingCurationFilter() {
        when(curationFilter.apply(any(EventProcessed.class))).thenReturn(FALSE);
    }

    private Map<String, StreamConfig> getStreamConfigsMap() {
        StreamConfig streamConfig = streamConfig(curationFilter);
        return singletonMap(streamConfig.getStreamName(), streamConfig);
    }
}
