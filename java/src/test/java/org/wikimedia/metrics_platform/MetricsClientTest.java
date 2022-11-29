package org.wikimedia.metrics_platform;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.context.ContextController;
import org.wikimedia.metrics_platform.curation.CurationController;

public class MetricsClientTest {

    private final ClientMetadata mockClientMetadata = mock(ClientMetadata.class);
    private final StreamConfigsFetcher mockStreamConfigFetcher = mock(StreamConfigsFetcher.class);
    private final EventSender mockEventSender = mock(EventSender.class);
    private final SessionController mockSessionController = mock(SessionController.class);
    private final SamplingController mockSamplingController = mock(SamplingController.class);
    private final ContextController mockContextController = mock(ContextController.class);
    private final CurationController mockCurationController = new AlwaysAcceptCurationController();

    private final MetricsClient client = new MetricsClient(
            mockClientMetadata,
            mockStreamConfigFetcher,
            mockEventSender,
            mockSessionController,
            mockSamplingController,
            mockContextController,
            mockCurationController,
            null,
            null, 10
    );

    @BeforeEach
    public void resetClient() {
        reset(mockClientMetadata, mockSessionController, mockSamplingController, mockContextController);
        client.setStreamConfigs(emptyMap());
    }

    @Test
    public void testTouchSessionOnApplicationPause() {
        client.onApplicationPause();
        verify(mockSessionController, times(1)).touchSession();
    }

    @Test
    public void testResumeSessionOnApplicationResume() {
        client.onApplicationResume();
        verify(mockSessionController, times(1)).touchSession();
    }

    @Test
    public void testAddEventMetadata() {
        Map<String, StreamConfig> streamConfigs = setStreamConfigs();
        when(mockSamplingController.isInSample(streamConfigs.get("test_event"))).thenReturn(true);

        Event event = new Event("test/event/1.0.0", "test_event", "testEvent");
        client.submit(event);

        verify(mockClientMetadata, times(1)).getAppInstallId();
        verify(mockSessionController, times(1)).getSessionId();
    }

    @Test
    public void testDispatchEvent() throws IOException {
        Map<String, StreamConfig> streamConfigs = setStreamConfigs();
        when(mockStreamConfigFetcher.fetchStreamConfigs()).thenReturn(getTestMetricsPlatformStreamConfigMap());
        when(mockSamplingController.isInSample(streamConfigs.get("test_stream"))).thenReturn(true);

        MetricsClient testClient = getClientToRunTaskImmediately();
        Map<String, Object> customDataMap = setTestCustomData();
        testClient.dispatch("test_event", customDataMap);
        // TODO: once the stream fetcher functionality is working, uncomment following assertion.
        //assertFalse(testClient.isPendingEventsEmpty());
        testClient.sendEnqueuedEvents();
        assertTrue(testClient.isPendingEventsEmpty());
    }

    @Test
    public void testFetchStreamConfigsTaskFetchesStreamConfigs() throws IOException {
        when(mockStreamConfigFetcher.fetchStreamConfigs()).thenReturn(getTestStreamConfigMap());

        client.new FetchStreamConfigsTask().run();
        // FIXME: Since configuration loading can happen either from a Timer (MetricsClient:348) or
        //  from the explicit call above, the fetchStreamConfigs() method can be called either once
        //  or twice. Fixing this properly will require some refactoring of the MetricsClient class.
        verify(mockStreamConfigFetcher, atLeastOnce()).fetchStreamConfigs();
    }

    @Test
    public void eventsNotSentWhenFetchStreamConfigFails() throws IOException {
        MetricsClient testClient = getClientToRunTaskImmediately();

        when(mockStreamConfigFetcher.fetchStreamConfigs()).thenThrow(IOException.class);

        testClient.new FetchStreamConfigsTask();
        testClient.submit(new Event("stream", "stream", "event"));

        verify(mockEventSender, never()).sendEvents(anyString(), anyCollection());
    }

    @Test
    public void testEventSubmissionTaskSendsEnqueuedEvents() throws Exception {
        setStreamConfigs();

        Event event = new Event("foo", "test_event", "testEvent");
        client.submit(event);
        client.new EventSubmissionTask().run();

        verify(mockEventSender, times(1)).sendEvents(anyString(), anyCollection());
    }

    @Test
    public void testEventsRemovedFromOutputBufferOnSuccess() {
        MetricsClient testClient = getClientToRunTaskImmediately();

        Map<String, StreamConfig> streamConfigs = new HashMap<>();
        StreamConfig streamConfig = new StreamConfig("test.event", "test/event", null, null);
        streamConfigs.put("test.event", streamConfig);
        testClient.setStreamConfigs(streamConfigs);

        Event event = new Event("test/event", "test.event", "testEvent");

        testClient.submit(event);
        testClient.sendEnqueuedEvents();
//      TODO:  assertThat(testClient.validatedEvents.isEmpty(), is(true));
    }

    @Test
    public void testEventsRemainInOutputBufferOnFailure() {
        ClientMetadata clientMetadata = new TestClientMetadata();
        StreamConfigsFetcher configsFetcher = new TestStreamConfigsFetcher(true);
        EventSender eventSender = new TestEventSender(true);
        SessionController sessionController = new SessionController();
        MetricsClient testClient = getClientToRunTaskImmediately();

        Map<String, StreamConfig> streamConfigs = new HashMap<>();
        StreamConfig streamConfig = new StreamConfig("test.event", "test/event", null, null);
        streamConfigs.put("test.event", streamConfig);
        testClient.setStreamConfigs(streamConfigs);

        Event event = new Event("test/event", "test.event", "testEvent");
        testClient.submit(event);
        testClient.sendEnqueuedEvents();
//      TODO:  assertThat(testClient.validatedEvents.isEmpty(), is(false));
    }

    /**
     * Convenience method for setting up stream configs.
     *
     * @return stream configs
     */
    private Map<String, StreamConfig> setStreamConfigs() {
        Map<String, StreamConfig> streamConfigs = getTestStreamConfigMap();
        client.setStreamConfigs(streamConfigs);
        return streamConfigs;
    }

    private static Map<String, StreamConfig> getTestStreamConfigMap() {
        StreamConfig streamConfig = new StreamConfig("test_event", "test/event", null, null);
        return singletonMap("test_event", streamConfig);
    }

    private static Map<String, StreamConfig> getTestMetricsPlatformStreamConfigMap() {
        StreamConfig streamConfig = new StreamConfig(
            "test_stream",
            "/analytics/mediawiki/client/metrics_event/1.1.0",
            DestinationEventService.ANALYTICS,
            null
        );
        return singletonMap("test_stream", streamConfig);
    }

    private static class AlwaysAcceptCurationController extends CurationController {
        @Override
        public boolean eventPassesCurationRules(Event event, StreamConfig streamConfig) {
            return true;
        }
    }

    /**
     * Convenience method for adding test custom data.
     *
     * @return Map<String, Object>
     */
    private Map<String, Object> setTestCustomData() {
        return new HashMap<String, Object>() {
            {
                put("action", "click");
                put("is_editor", true);
                put("screen_size", 1080);
            }
        };
    }

    private MetricsClient getClientToRunTaskImmediately() {
        ClientMetadata clientMetadata = new TestClientMetadata();
        StreamConfigsFetcher configsFetcher = new TestStreamConfigsFetcher(true);
        EventSender eventSender = new TestEventSender(true);
        SessionController sessionController = new SessionController();

        return new MetricsClient(
            clientMetadata,
            configsFetcher,
            eventSender,
            sessionController,
            new SamplingController(clientMetadata, sessionController),
            mockContextController,
            mockCurationController,
            new TimerTask() {
                @Override
                public void run() {
                }
            },
            new TimerTask() {
                @Override
                public void run() {
                }
            }, 10
        );
    }
}
