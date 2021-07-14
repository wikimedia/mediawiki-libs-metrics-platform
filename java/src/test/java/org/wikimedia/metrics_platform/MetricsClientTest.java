package org.wikimedia.metrics_platform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.context.ContextController;
import org.wikimedia.metrics_platform.curation.CurationController;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MetricsClientTest {

    private final MetricsClientIntegration mockIntegration = mock(MetricsClientIntegration.class);
    private final SessionController mockSessionController = mock(SessionController.class);
    private final SamplingController mockSamplingController = mock(SamplingController.class);
    private final ContextController mockContextController = mock(ContextController.class);
    private final CurationController mockCurationController = mock(CurationController.class);
    private final Queue<Event> mockInputBuffer = mock(Queue.class);
    private final ArrayList<Event> mockOutputBuffer = mock(ArrayList.class);

    private final MetricsClient client = new MetricsClient(
            mockIntegration,
            mockSessionController,
            mockSamplingController,
            mockContextController,
            mockCurationController,
            mockInputBuffer,
            mockOutputBuffer,
            null,
            null
    );

    @BeforeEach
    public void resetClient() {
        reset(mockIntegration, mockSessionController, mockSamplingController, mockInputBuffer, mockOutputBuffer);
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
    public void testEventSentToInputBufferOnSubmit() {
        Event event = new Event("test/event/1.0.0", "test_event");
        client.submit(event, "test_event");
        verify(mockInputBuffer, times(1)).add(event);
        verify(mockOutputBuffer, times(0)).add(any(Event.class));
    }

    @Test
    public void testEventSentToOutputBufferOnSubmit() {
        Map<String, StreamConfig> streamConfigs = setStreamConfigs();
        when(mockSamplingController.isInSample(streamConfigs.get("test_event"))).thenReturn(true);
        when(mockCurationController.eventPassesCurationRules(any(Event.class), any(StreamConfig.class)))
                .thenReturn(true);

        Event event = new Event("test/event/1.0.0", "test_event");
        client.submit(event, "test_event");
        verify(mockInputBuffer, times(0)).add(any(Event.class));
        verify(mockOutputBuffer, times(1)).add(event);
    }

    @Test
    public void testMoveInputBufferEventsToOutputBuffer() {
        Map<String, StreamConfig> streamConfigs = setStreamConfigs();
        when(mockSamplingController.isInSample(streamConfigs.get("test_event"))).thenReturn(true);
        when(mockInputBuffer.remove()).thenReturn(
                new Event("test/event/1.0.0", "test_event"),
                new Event("no/such/stream", "no_such_stream")
        );
        when(mockInputBuffer.isEmpty()).thenReturn(false).thenReturn(false).thenReturn(true);
        when(mockCurationController.eventPassesCurationRules(any(Event.class), any(StreamConfig.class)))
                .thenReturn(true);
        client.moveInputBufferEventsToOutputBuffer();
        verify(mockOutputBuffer, times(1)).add(any(Event.class));
    }

    @Test
    public void testFetchStreamConfigsTaskFetchesStreamConfigs() {
        client.new FetchStreamConfigsTask().run();
        verify(mockIntegration, times(1)).fetchStreamConfigs(any(MetricsClientIntegration.FetchStreamConfigsCallback.class));
    }

    @Test
    public void testEventSubmissionTaskSendsEnqueuedEvents() {
        setStreamConfigs();

        Event event = new Event("foo", "bar");
        when(mockOutputBuffer.clone()).thenReturn(new ArrayList<>(Collections.singletonList(event)));
        when(mockOutputBuffer.isEmpty()).thenReturn(false, true);

        client.new EventSubmissionTask().run();
        verify(mockIntegration, times(1)).sendEvents(anyString(), anyCollection(), any(MetricsClientIntegration.SendEventsCallback.class));
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
                mockInputBuffer,
                new ArrayList<>(),
                new TimerTask() { @Override public void run() { } },
                new TimerTask() { @Override public void run() { } }
        );

        Map<String, StreamConfig> streamConfigs = new HashMap<>();
        StreamConfig streamConfig = new StreamConfig("test.event", "test/event", null, null);
        streamConfigs.put("test.event", streamConfig);
        testClient.setStreamConfigs(streamConfigs);

        Event event = new Event("test/event", "test.event");
        when(mockCurationController.eventPassesCurationRules(event, streamConfig)).thenReturn(true);

        testClient.submit(event, "test.event");
        testClient.sendEnqueuedEvents();
        assertThat(testClient.outputBuffer.isEmpty(), is(true));
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
                mockInputBuffer,
                new ArrayList<>(),
                new TimerTask() { @Override public void run() { } },
                new TimerTask() { @Override public void run() { } }
        );

        Map<String, StreamConfig> streamConfigs = new HashMap<>();
        StreamConfig streamConfig = new StreamConfig("test.event", "test/event", null, null);
        streamConfigs.put("test.event", streamConfig);
        testClient.setStreamConfigs(streamConfigs);

        Event event = new Event("test/event", "test.event");
        when(mockCurationController.eventPassesCurationRules(event, streamConfig)).thenReturn(true);

        testClient.submit(event, "test.event");
        testClient.sendEnqueuedEvents();
        assertThat(testClient.outputBuffer.isEmpty(), is(false));
    }

    /**
     * Convenience method for setting up stream configs.
     * @return stream configs
     */
    private Map<String, StreamConfig> setStreamConfigs() {
        StreamConfig streamConfig = new StreamConfig("test_event", "test/event", null, null);
        Map<String, StreamConfig> streamConfigs = new HashMap<String, StreamConfig>(){{  put("test_event", streamConfig); }};
        client.setStreamConfigs(streamConfigs);
        return streamConfigs;
    }

}
