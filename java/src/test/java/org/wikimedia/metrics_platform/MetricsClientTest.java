package org.wikimedia.metrics_platform;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.TimerTask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.context.ContextController;
import org.wikimedia.metrics_platform.curation.CurationController;

public class MetricsClientTest {

    private final MetricsClientIntegration mockIntegration = mock(MetricsClientIntegration.class);
    private final SessionController mockSessionController = mock(SessionController.class);
    private final SamplingController mockSamplingController = mock(SamplingController.class);
    private final ContextController mockContextController = mock(ContextController.class);
    private final CurationController mockCurationController = mock(CurationController.class);
    private final Queue<Event> mockUnvalidatedEvents = mock(Queue.class);
    private final EventBuffer mockValidatedEvents = mock(EventBuffer.class);
    private final EventBuffer mockValidatedErrors = mock(EventBuffer.class);

    private final MetricsClient client = new MetricsClient(
            mockIntegration,
            mockSessionController,
            mockSamplingController,
            mockContextController,
            mockCurationController,
            mockUnvalidatedEvents,
            mockValidatedEvents,
            mockValidatedErrors,
            null,
            null
    );

    @BeforeEach
    public void resetClient() {
        reset(mockIntegration, mockSessionController, mockSamplingController, mockUnvalidatedEvents,
                mockValidatedEvents, mockValidatedErrors);
        client.setStreamConfigs(Collections.emptyMap());
    }

    @Test
    public void testTouchSessionOnApplicationPause() {
        client.onApplicationPause();
        verify(mockSessionController, times(1)).touchSession();
    }

    @Test
    public void testBeginNewSessionOnApplicationResume() {
        when(mockSessionController.sessionExpired()).thenReturn(true);
        client.onApplicationResume();
        verify(mockSessionController, times(1)).beginNewSession();
    }

    @Test
    public void testResumeSessionOnApplicationResume() {
        when(mockSessionController.sessionExpired()).thenReturn(false);
        client.onApplicationResume();
        verify(mockSessionController, times(0)).beginNewSession();
        verify(mockSessionController, times(1)).touchSession();
    }

    @Test
    public void testAddEventMetadata() {
        Map<String, StreamConfig> streamConfigs = setStreamConfigs();
        when(mockSamplingController.isInSample(streamConfigs.get("test_event"))).thenReturn(true);

        Event event = new Event("test/event/1.0.0", "test_event");
        client.submit(event, "test_event");
        verify(mockIntegration, times(1)).getAppInstallId();
        verify(mockSessionController, times(1)).getSessionId();
    }

    @Test
    public void testShouldProcessEventsForStream() {
        Map<String, StreamConfig> streamConfigs = setStreamConfigs();
        streamConfigs.put("test_event_2", new StreamConfig("test_event_2", "test/event/2", null, null));
        when(mockSamplingController.isInSample(streamConfigs.get("test_event"))).thenReturn(true);
        when(mockSamplingController.isInSample(streamConfigs.get("test_event_2"))).thenReturn(false);

        assertThat(client.shouldProcessEventsForStream("test_event"), is(true));
        assertThat(client.shouldProcessEventsForStream("test_event_2"), is(false));
        assertThat(client.shouldProcessEventsForStream("no_such_stream"), is(false));
    }

    @Test
    public void testSubmitEventWithNoStreamConfigs() {
        Event event = new Event("test/event/1.0.0", "test_event");
        client.submit(event, "test_event");
        verify(mockUnvalidatedEvents, times(1)).add(event);
        verify(mockValidatedEvents, times(0)).add(any(Event.class));
    }

    @Test
    public void testEventSentToOutputBufferOnSubmit() {
        Map<String, StreamConfig> streamConfigs = setStreamConfigs();
        when(mockSamplingController.isInSample(streamConfigs.get("test_event"))).thenReturn(true);
        when(mockCurationController.eventPassesCurationRules(any(Event.class), any(StreamConfig.class)))
                .thenReturn(true);

        Event event = new Event("test/event/1.0.0", "test_event");
        client.submit(event, "test_event");
        verify(mockUnvalidatedEvents, times(0)).add(any(Event.class));
        verify(mockValidatedEvents, times(1)).add(event);
    }

    @Test
    public void testProcessUnvalidatedEvents() {
        Map<String, StreamConfig> streamConfigs = setStreamConfigs();
        when(mockSamplingController.isInSample(streamConfigs.get("test_event"))).thenReturn(true);
        when(mockUnvalidatedEvents.remove()).thenReturn(
                new Event("test/event/1.0.0", "test_event"),
                new Event("no/such/stream", "no_such_stream")
        );
        when(mockUnvalidatedEvents.isEmpty()).thenReturn(false).thenReturn(false).thenReturn(true);
        when(mockCurationController.eventPassesCurationRules(any(Event.class), any(StreamConfig.class)))
                .thenReturn(true);
        client.processUnvalidatedEvents();
        verify(mockValidatedEvents, times(1)).add(any(Event.class));
    }

    @Test
    public void testFetchStreamConfigsTaskFetchesStreamConfigs() throws IOException {
        client.new FetchStreamConfigsTask().run();
        // FIXME: Since configuration loading can happen either from a Timer (MetricsClient:348) or
        //  from the explicit call above, the fetchStreamConfigs() method can be called either once
        //  or twice. Fixing this properly will require some refactoring of the MetricsClient class.
        verify(mockIntegration).fetchStreamConfigs();
        when(mockIntegration.fetchStreamConfigs()).thenReturn(setStreamConfigs());
    }

    @Test
    public void testValidatedEventsNotAddedWhenFetchStreamConfigFails() {
        MetricsClient testClient = new MetricsClient(
                mockIntegration,
                mockSessionController,
                new SamplingController(mockIntegration, mockSessionController),
                mockContextController,
                mockCurationController,
                mockUnvalidatedEvents,
                new EventBuffer(DestinationEventService.ANALYTICS),
                new EventBuffer(DestinationEventService.ERROR_LOGGING),
                new TimerTask() {
                    @Override
                    public void run() {
                    }
                },
                new TimerTask() {
                    @Override
                    public void run() {
                    }
                }
        );

        try {
            when(mockIntegration.fetchStreamConfigs()).thenThrow(IOException.class);
            testClient.new FetchStreamConfigsTask();
            testClient.submit(new Event("stream", "stream"), "stream");

            verify(mockValidatedEvents, never()).add(any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEventSubmissionTaskSendsEnqueuedEvents() throws Exception {
        setStreamConfigs();

        Event event = new Event("foo", "bar");
        when(mockValidatedEvents.peekAll()).thenReturn(new ArrayList<>(Collections.singletonList(event)));
        when(mockValidatedEvents.getDestinationService()).thenReturn(DestinationEventService.ANALYTICS);
        when(mockValidatedEvents.isEmpty()).thenReturn(false, true);
        when(mockValidatedErrors.peekAll()).thenReturn(Collections.emptyList());
        when(mockValidatedErrors.getDestinationService()).thenReturn(DestinationEventService.ERROR_LOGGING);
        when(mockValidatedErrors.isEmpty()).thenReturn(true);

        client.new EventSubmissionTask().run();
        verify(mockIntegration, times(1)).sendEvents(anyString(), anyCollection());
    }

    @Test
    public void testEventsRemovedFromOutputBufferOnSuccess() {
        MetricsClientIntegration integration = new TestMetricsClientIntegration();
        SessionController sessionController = new SessionController();

        MetricsClient testClient = new MetricsClient(
                integration,
                sessionController,
                new SamplingController(integration, sessionController),
                mockContextController,
                mockCurationController,
                mockUnvalidatedEvents,
                new EventBuffer(DestinationEventService.ANALYTICS),
                new EventBuffer(DestinationEventService.ERROR_LOGGING),
                new TimerTask() {
                    @Override
                    public void run() {
                    }
                },
                new TimerTask() {
                    @Override
                    public void run() {
                    }
                }
        );

        Map<String, StreamConfig> streamConfigs = new HashMap<>();
        StreamConfig streamConfig = new StreamConfig("test.event", "test/event", null, null);
        streamConfigs.put("test.event", streamConfig);
        testClient.setStreamConfigs(streamConfigs);

        Event event = new Event("test/event", "test.event");
        when(mockCurationController.eventPassesCurationRules(event, streamConfig)).thenReturn(true);

        testClient.submit(event, "test.event");
        testClient.sendEnqueuedEvents();
        assertThat(testClient.validatedEvents.isEmpty(), is(true));
    }

    @Test
    public void testEventsRemainInOutputBufferOnFailure() {
        MetricsClientIntegration integration = new TestMetricsClientIntegration(true);
        SessionController sessionController = new SessionController();

        MetricsClient testClient = new MetricsClient(
                integration,
                sessionController,
                new SamplingController(integration, sessionController),
                mockContextController,
                mockCurationController,
                mockUnvalidatedEvents,
                new EventBuffer(DestinationEventService.ANALYTICS),
                new EventBuffer(DestinationEventService.ERROR_LOGGING),
                new TimerTask() {
                    @Override
                    public void run() {
                    }
                },
                new TimerTask() {
                    @Override
                    public void run() {
                    }
                }
        );

        Map<String, StreamConfig> streamConfigs = new HashMap<>();
        StreamConfig streamConfig = new StreamConfig("test.event", "test/event", null, null);
        streamConfigs.put("test.event", streamConfig);
        testClient.setStreamConfigs(streamConfigs);

        Event event = new Event("test/event", "test.event");
        when(mockCurationController.eventPassesCurationRules(event, streamConfig)).thenReturn(true);

        testClient.submit(event, "test.event");
        testClient.sendEnqueuedEvents();
        assertThat(testClient.validatedEvents.isEmpty(), is(false));
    }

    /**
     * Convenience method for setting up stream configs.
     *
     * @return stream configs
     */
    private Map<String, StreamConfig> setStreamConfigs() {
        StreamConfig streamConfig = new StreamConfig("test_event", "test/event", null, null);
        Map<String, StreamConfig> streamConfigs = new HashMap<>();
        streamConfigs.put("test_event", streamConfig);
        client.setStreamConfigs(streamConfigs);
        return streamConfigs;
    }

}
