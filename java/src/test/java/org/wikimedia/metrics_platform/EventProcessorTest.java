package org.wikimedia.metrics_platform;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.wikimedia.metrics_platform.MetricsClientTest.getTestSourceConfig;
import static org.wikimedia.metrics_platform.MetricsClientTest.getTestStreamConfig;
import static org.wikimedia.metrics_platform.config.CurationFilterFixtures.getCurationFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.context.PerformerData;
import org.wikimedia.metrics_platform.event.Event;

@ExtendWith(MockitoExtension.class)
class EventProcessorTest {
    private ContextController contextController = new ContextController(new TestClientMetadata());
    @Mock private EventSender mockEventSender;
    private AtomicReference<SourceConfig> sourceConfig;
    private BlockingQueue<Event> eventQueue;
    private EventProcessor eventProcessor;
    private Event event;
    private static final CurationFilter curationFilter = getCurationFilter();

    @BeforeEach void createEventProcessor() {
        sourceConfig = new AtomicReference<>(getTestSourceConfig());
        eventQueue = new LinkedBlockingQueue<>(10);

        eventProcessor = new EventProcessor(
                contextController,
                sourceConfig,
                mockEventSender,
                eventQueue
        );
    }

    @BeforeEach void resetEvent() {
        event = new Event("test_schema", "test_stream", "test_event");
    }

    @Test void testSendEnqueuedEvents() throws IOException {
        eventQueue.offer(event);
        eventProcessor.sendEnqueuedEvents();
        verify(mockEventSender).sendEvents(anyString(), anyCollection());
    }

    @Test void testEventsRemovedFromOutputBufferOnSuccess() {
        eventQueue.offer(event);
        eventProcessor.sendEnqueuedEvents();
        assertThat(eventQueue).isEmpty();
    }

    @Test void testEventsRemainInOutputBufferOnFailure() throws IOException {
        doThrow(IOException.class).when(mockEventSender).sendEvents(anyString(), anyCollection());
        eventQueue.offer(event);
        eventProcessor.sendEnqueuedEvents();
        assertThat(eventQueue).isNotEmpty();
    }

    @Test void eventsAreEnrichedBeforeBeingSent() throws IOException {
        ArgumentCaptor<Collection<Event>> eventCaptor = ArgumentCaptor.forClass(Collection.class);
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

    @Test void testEventPassesCurationFilters() {
        PerformerData performerData = new PerformerData();
        performerData.setGroups(Collections.singleton("steward"));
        event.getPageData().setTitle("Test");
        event.setPerformerData(performerData);
        assertThat(eventProcessor.eventPassesCurationRules(event, getStreamConfigsMap())).isTrue();
    }

    @Test void testEventFailsEqualsRule() {
        ClientMetadata anotherTestClientMetadata = new TestClientMetadata() {
            @Override
            public String getPageTitle() {
                return "Not Test";
            }
        };
        eventProcessor = new EventProcessor(
                new ContextController(anotherTestClientMetadata),
                sourceConfig,
                mockEventSender,
                eventQueue
        );
        assertThat(eventProcessor.eventPassesCurationRules(event, getStreamConfigsMap())).isFalse();
    }

    @Test void testEventFailsCollectionContainsAnyRule() {
        PerformerData performerData = new PerformerData();
        performerData.setGroups(Collections.singleton("*"));
        event.getPageData().setTitle("Test");
        event.setPerformerData(performerData);
        assertThat(eventProcessor.eventPassesCurationRules(event, getStreamConfigsMap())).isFalse();
    }

    @Test void testEventFailsCollectionDoesNotContainRule() {
        PerformerData performerData = new PerformerData();
        performerData.setGroups(new HashSet<>(Arrays.asList("steward", "sysop")));
        event.getPageData().setTitle("Test");
        event.setPerformerData(performerData);
        assertThat(eventProcessor.eventPassesCurationRules(event, getStreamConfigsMap())).isFalse();
    }

    private static Map<String, StreamConfig> getStreamConfigsMap() {
        StreamConfig streamConfig = getTestStreamConfig(curationFilter);
        return singletonMap(streamConfig.getStreamName(), streamConfig);
    }
}
