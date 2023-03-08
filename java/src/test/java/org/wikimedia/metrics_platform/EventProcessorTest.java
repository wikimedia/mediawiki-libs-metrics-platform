package org.wikimedia.metrics_platform;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.wikimedia.metrics_platform.MetricsClientTest.getTestSourceConfig;
import static org.wikimedia.metrics_platform.MetricsClientTest.getTestStreamConfig;
import static org.wikimedia.metrics_platform.event.EventFixtures.minimalEvent;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
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
    @Mock private EventSender mockEventSender;
    @Mock private CurationFilter curationFilter;
    private final AtomicReference<SourceConfig> sourceConfig = new AtomicReference<>();
    private final BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>(10);
    private EventProcessor eventProcessor;

    @BeforeEach void clearEventQueue() {
        eventQueue.clear();
    }

    @BeforeEach void createEventProcessor() {
        sourceConfig.set(getTestSourceConfig(curationFilter));

        eventProcessor = new EventProcessor(
                new ContextController(new TestClientMetadata()),
                sourceConfig,
                mockEventSender,
                eventQueue
        );
    }

    @Test void enqueuedEventsAreSent() throws IOException {
        whenEventsArePassingCurationFilter();

        eventQueue.offer(minimalEvent());
        eventProcessor.sendEnqueuedEvents();
        verify(mockEventSender).sendEvents(anyString(), anyCollection());
    }

    @Test void eventsNotPassingCurationFiltersAreDropped() throws IOException {
        whenEventsAreNotPassingCurationFilter();

        eventQueue.offer(minimalEvent());
        eventProcessor.sendEnqueuedEvents();

        verify(mockEventSender, never()).sendEvents(anyString(), anyCollection());
        assertThat(eventQueue).isEmpty();
    }

    @Test void eventsAreRemovedFromQueueOnceSent() {
        whenEventsArePassingCurationFilter();

        eventQueue.offer(minimalEvent());
        eventProcessor.sendEnqueuedEvents();

        assertThat(eventQueue).isEmpty();
    }

    @Test void eventsRemainInOutputBufferOnFailure() throws IOException {
        whenEventsArePassingCurationFilter();
        doThrow(IOException.class).when(mockEventSender).sendEvents(anyString(), anyCollection());

        eventQueue.offer(minimalEvent());
        eventProcessor.sendEnqueuedEvents();

        assertThat(eventQueue).isNotEmpty();
    }

    @Test void eventsAreEnrichedBeforeBeingSent() throws IOException {
        whenEventsArePassingCurationFilter();

        eventQueue.offer(minimalEvent());
        eventProcessor.sendEnqueuedEvents();

        ArgumentCaptor<Collection<Event>> eventCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(mockEventSender).sendEvents(anyString(), eventCaptor.capture());

        Event sentEvent = eventCaptor.getValue().iterator().next();
        assertThat(sentEvent.getPageData().getTitle()).isEqualTo("Test");
    }

    @Test void eventsNotSentWhenFetchStreamConfigFails() {
        sourceConfig.set(null);

        eventQueue.offer(minimalEvent());
        eventProcessor.sendEnqueuedEvents();

        assertThat(eventQueue).isNotEmpty();
    }

    @Test void testEventPassesCurationFilters() {
        whenEventsArePassingCurationFilter();

        // FIXME: move this to CurationFilterTest
        Event event = minimalEvent();
        event.getPageData().setTitle("Test");

        event.setPerformerData(
                PerformerData.builder()
                        .groups(singleton("steward"))
                        .build()
        );

        assertThat(eventProcessor.eventPassesCurationRules(event, getStreamConfigsMap())).isTrue();
    }

    @Test void testEventFailsEqualsRule() {

        // FIXME: move this to CurationControllerTest
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
        assertThat(eventProcessor.eventPassesCurationRules(minimalEvent(), getStreamConfigsMap())).isFalse();
    }

    @Test void testEventFailsCollectionContainsAnyRule() {

        // FIXME: move to CurationControllerTest
        PerformerData performerData = new PerformerData();
        performerData.setGroups(singleton("*"));
        Event event = minimalEvent();
        event.getPageData().setTitle("Test");
        event.setPerformerData(performerData);
        assertThat(eventProcessor.eventPassesCurationRules(event, getStreamConfigsMap())).isFalse();
    }

    @Test void testEventFailsCollectionDoesNotContainRule() {
        // FIXME: move to CurationControllerTest
        PerformerData performerData = new PerformerData();
        performerData.setGroups(new HashSet<>(Arrays.asList("steward", "sysop")));
        Event event = minimalEvent();
        event.getPageData().setTitle("Test");
        event.setPerformerData(performerData);
        assertThat(eventProcessor.eventPassesCurationRules(event, getStreamConfigsMap())).isFalse();
    }

    private void whenEventsArePassingCurationFilter() {
        when(curationFilter.apply(any(Event.class))).thenReturn(TRUE);
    }

    private void whenEventsAreNotPassingCurationFilter() {
        when(curationFilter.apply(any(Event.class))).thenReturn(FALSE);
    }

    private Map<String, StreamConfig> getStreamConfigsMap() {
        StreamConfig streamConfig = getTestStreamConfig(curationFilter);
        return singletonMap(streamConfig.getStreamName(), streamConfig);
    }
}
