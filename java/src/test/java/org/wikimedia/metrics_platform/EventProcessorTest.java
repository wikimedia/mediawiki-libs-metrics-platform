package org.wikimedia.metrics_platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.wikimedia.metrics_platform.MetricsClientTest.getTestSourceConfig;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wikimedia.metrics_platform.config.SourceConfig;
import org.wikimedia.metrics_platform.config.StreamConfigFetcher;
import org.wikimedia.metrics_platform.context.ContextController;
import org.wikimedia.metrics_platform.curation.CurationController;

@ExtendWith(MockitoExtension.class)
class EventProcessorTest {

    private ContextController mockContextController = new ContextController(new TestClientMetadata());
    @Mock private EventSender mockEventSender;
    @Mock private CurationController mockCurationController;
    private AtomicReference<SourceConfig> sourceConfig;
    private BlockingQueue<Event> eventQueue;
    private EventProcessor eventProcessor;

    @BeforeEach void createEventProcessor() {
        sourceConfig = new AtomicReference<>(getTestSourceConfig());
        eventQueue = new LinkedBlockingQueue<>(10);

        eventProcessor = new EventProcessor(
                mockContextController,
                mockCurationController,
                sourceConfig,
                mockEventSender,
                eventQueue
        );
    }

    @Test void testSendEnqueuedEvents() throws IOException {
        doReturn(true).when(mockCurationController).eventPassesCurationRules(any(), any());

        Event event = new Event("test_schema", "test_stream", "test_event");
        eventQueue.offer(event);
        eventProcessor.sendEnqueuedEvents();

        verify(mockEventSender).sendEvents(anyString(), anyCollection());
    }

    @Test void testEventsRemovedFromOutputBufferOnSuccess() {
        doReturn(true).when(mockCurationController).eventPassesCurationRules(any(), any());

        Event event = new Event(StreamConfigFetcher.METRICS_PLATFORM_SCHEMA_TITLE, "test_stream", "test_event");
        eventQueue.offer(event);
        eventProcessor.sendEnqueuedEvents();

        assertThat(eventQueue).isEmpty();
    }

    @Test void testEventsRemainInOutputBufferOnFailure() throws IOException {
        doReturn(true).when(mockCurationController).eventPassesCurationRules(any(), any());
        doThrow(IOException.class).when(mockEventSender).sendEvents(anyString(), anyCollection());
        Event event = new Event("test_schema", "test_stream", "test_event");
        eventQueue.offer(event);

        eventProcessor.sendEnqueuedEvents();

        assertThat(eventQueue).isNotEmpty();
    }

    @Test void eventsAreEnrichedBeforeBeingSent() throws IOException {
        doReturn(true).when(mockCurationController).eventPassesCurationRules(any(), any());
        ArgumentCaptor<Collection<Event>> eventCaptor = ArgumentCaptor.forClass(Collection.class);

        Event event = new Event("test_schema", "test_stream", "test_event");
        eventQueue.offer(event);

        eventProcessor.sendEnqueuedEvents();

        verify(mockEventSender).sendEvents(anyString(), eventCaptor.capture());

        Event sentEvent = eventCaptor.getValue().iterator().next();
        assertThat(sentEvent.getPageData().getTitle()).isEqualTo("Test");
    }

    @Test void eventsNotSentWhenFetchStreamConfigFails() throws Exception {
        sourceConfig.set(null);
        eventQueue.offer(new Event("stream", "stream", "event"));

        eventProcessor.sendEnqueuedEvents();

        assertThat(eventQueue).isNotEmpty();
    }
}
