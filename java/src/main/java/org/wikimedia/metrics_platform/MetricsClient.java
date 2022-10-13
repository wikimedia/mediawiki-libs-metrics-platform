package org.wikimedia.metrics_platform;

import static java.time.Instant.now;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.groupingBy;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.wikimedia.metrics_platform.context.ContextController;
import org.wikimedia.metrics_platform.curation.CurationController;

public final class MetricsClient {

    private final BlockingQueue<Event> pendingEvents;

    public static MetricsClient getInstance(ClientMetadata clientMetadata,
                                            StreamConfigsFetcher streamConfigsFetcher,
                                            EventSender eventSender) {
        return new MetricsClient(clientMetadata, streamConfigsFetcher, eventSender);
    }

    /**
     * Stream configs to be fetched on startup and stored for the duration of the app lifecycle.
     */
    private final AtomicReference<Map<String, StreamConfig>> streamConfigsReference = new AtomicReference<>(emptyMap());

    private static final int STREAM_CONFIG_FETCH_ATTEMPT_INTERVAL = 30000; // 30 seconds

    /**
     * Integration layer exposing hosting application functionality to the client library.
     */
    private final ClientMetadata clientMetadata;

    private final StreamConfigsFetcher streamConfigsFetcher;

    private final EventSender eventSender;

    /**
     * Handles logging session management. A new session begins (and a new session ID is created)
     * if the app has been inactive for 15 minutes or more.
     */
    private final SessionController sessionController;

    /**
     * Evaluates whether events for a given stream are in-sample based on the stream configuration.
     */
    private final SamplingController samplingController;

    /**
     * Enriches event data with context data requested in the stream configuration.
     */
    private final ContextController contextController;

    /**
     * Applies stream data curation rules specified in the stream configuration.
     */
    private final CurationController curationController;

    private static final int SEND_INTERVAL = 30000; // 30 seconds


    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT)
            .withZone(ZoneId.of("UTC"));
    private static final Timer TIMER = new Timer();

    /**
     * Submit an event to be enqueued and sent to the Event Platform.
     * <p>
     * If stream configs are not yet fetched, the event will be held temporarily in the input
     * buffer (provided there is space to do so).
     * <p>
     * If stream configs are available, the event will be validated and enqueued for submission
     * to the configured event platform intake service.
     * <p>
     * Supplemental metadata is added immediately on intake, regardless of the presence or absence
     * of stream configs, so that the event timestamp is recorded accurately.
     *
     * @param event  event data
     */
    public void submit(Event event) {
        addRequiredMetadata(event);
        pendingEvents.add(event);
    }

    /**
     * Convenience method to be called when
     * <a href="https://developer.android.com/guide/components/activities/activity-lifecycle#onpause">
     * the onPause() activity lifecycle callback</a> is called.
     *
     * Touches the session so that we can determine whether it session has expired if and when the
     * application is resumed.
     */
    public void onApplicationPause() {
        sessionController.touchSession();
    }

    /**
     * Convenience method to be called when
     * <a href="https://developer.android.com/guide/components/activities/activity-lifecycle#onresume">
     * the onResume() activity lifecycle callback</a> is called.
     *
     * Touches the session so that we can determine whether it has expired.
     */
    public void onApplicationResume() {
        sessionController.touchSession();
    }

    /**
     * Setter exposed for testing.
     *
     * @param streamConfigsReference stream configs
     */
    void setStreamConfigs(@Nonnull Map<String, StreamConfig> streamConfigsReference) {
        this.streamConfigsReference.set(streamConfigsReference);
    }

    /**
     * Returns true if the specified stream is configured and in sample.
     * <p>
     * Visible for testing.
     *
     * @param stream stream name
     * @return boolean
     */
    // Todo: include condition in filter when sendingEnqueuedEvents
    boolean shouldProcessEventsForStream(String stream) {
        StreamConfig streamConfig = streamConfigsReference.get().get(stream);
        return streamConfig != null && samplingController.isInSample(streamConfig);
    }

    /**
     * Fetch stream configs and hold them in memory. After stream configs are fetched, move any
     * events in the input buffer over to the output buffer.
     */
    private void fetchStreamConfigs() {
        try {
            Map<String, StreamConfig> streamConfig = streamConfigsFetcher.fetchStreamConfigs();
            if (!streamConfig.isEmpty()) setStreamConfigs(streamConfig);
        } catch (IOException ignore) {
            // TODO: decide what to do with logging
        }
    }

    /**
     * Supplement the outgoing event with additional metadata.
     * These include:
     * - app_install_id: app install ID
     * - app_session_id: the current session ID
     * - dt: ISO 8601 timestamp
     *
     * @param event event
     */
    private void addRequiredMetadata(Event event) {
        event.setAppInstallId(clientMetadata.getAppInstallId());
        event.setAppSessionId(sessionController.getSessionId());
        event.setTimestamp(DATE_FORMAT.format(now()));
    }

    /**
     * Send all events currently in the output buffer.
     * <p>
     * A shallow clone of the output buffer is created and passed to the integration layer for
     * submission by the client. If the event submission succeeds, the events are removed from the
     * output buffer. (Note that the shallow copy created by clone() retains pointers to the original
     * Event objects.) If the event submission fails, a client error is produced, and the events remain
     * in buffer to be retried on the next submission attempt.
     * <p>
     * TODO: Add client error logging.
     */
    void sendEnqueuedEvents() {
        Map<String, StreamConfig> streamConfigMap = streamConfigsReference.get();

        ArrayList<Event> pending = new ArrayList<>();
        this.pendingEvents.drainTo(pending);

        pending.stream()
                .filter(event -> eventPassesCurationRules(event, streamConfigMap))
                .collect(groupingBy(event -> destinationEventService(event, streamConfigMap), Collectors.toList()))
                .forEach(this::sendEventsToDestination);
    }

    private boolean eventPassesCurationRules(Event event, Map<String, StreamConfig> streamConfigMap) {
        // Todo: what to do when stream config is null
        StreamConfig streamConfig = streamConfigMap.get(event.getStream());
        contextController.addRequestedValues(event, streamConfig);
        return curationController.eventPassesCurationRules(event, streamConfig);
    }

    private DestinationEventService destinationEventService(Event event, Map<String, StreamConfig> streamConfigMap) {
        StreamConfig streamConfig = streamConfigMap.get(event.getStream());
        return streamConfig.getDestinationEventService();
    }

    private void sendEventsToDestination(DestinationEventService destinationEventService, List<Event> pendingValidEvents) {
        try {
            eventSender.sendEvents(destinationEventService.getBaseUri(), pendingValidEvents);
        } catch (IOException ignore) {
            MetricsClient.this.pendingEvents.addAll(pendingValidEvents);
        }
    }

    /**
     * MetricsClient Constructor.
     *
     * @param ClientMetadata clientMetadata implementation
     */
    MetricsClient(ClientMetadata clientMetadata,
                  StreamConfigsFetcher streamConfigsFetcher,
                  EventSender eventSender) {
        this(clientMetadata, streamConfigsFetcher, eventSender, new SessionController());
    }

    /**
     * @param clientMetadata       clientMetadata
     * @param sessionController session controller
     */
    MetricsClient(ClientMetadata clientMetadata,
                  StreamConfigsFetcher streamConfigsFetcher,
                  EventSender eventSender,
                  SessionController sessionController) {
        this(
                clientMetadata,
                streamConfigsFetcher,
                eventSender,
                sessionController,
                new SamplingController(clientMetadata, sessionController),
                new ContextController(clientMetadata)
        );
    }

    private MetricsClient(
            ClientMetadata clientMetadata,
            StreamConfigsFetcher streamConfigsFetcher,
            EventSender eventSender,
            SessionController sessionController,
            SamplingController samplingController,
            ContextController contextController
    ) {
        this(
                clientMetadata,
                streamConfigsFetcher,
                eventSender,
                sessionController,
                samplingController,
                contextController,
                new CurationController(),
                null,
                null,
                10
        );
    }

    /**
     * Constructor for testing.
     *
     * @param clientMetadata        clientMetadata
     * @param streamConfigsFetcher   stream config fetcher
     * @param eventSender           event sender
     * @param sessionController      session controller
     * @param samplingController     sampling controller
     * @param contextController      context controller
     * @param curationController     curation controller
     * @param fetchStreamConfigsTask optional custom implementation of stream configs fetch task (for testing)
     * @param eventSubmissionTask    optional custom implementation of event submission task (for testing)
     * @param capacity               queue capacity
     */
    MetricsClient(
            ClientMetadata clientMetadata,
            StreamConfigsFetcher streamConfigsFetcher,
            EventSender eventSender,
            SessionController sessionController,
            SamplingController samplingController,
            ContextController contextController,
            CurationController curationController,
            TimerTask fetchStreamConfigsTask,
            TimerTask eventSubmissionTask,
            int capacity) {
        this.clientMetadata = clientMetadata;
        this.sessionController = sessionController;
        this.samplingController = samplingController;
        this.contextController = contextController;
        this.curationController = curationController;
        this.pendingEvents = new LinkedBlockingQueue<>(capacity);
        this.streamConfigsFetcher = streamConfigsFetcher;
        this.eventSender = eventSender;

        TIMER.schedule(fetchStreamConfigsTask != null ? fetchStreamConfigsTask : new FetchStreamConfigsTask(), 0, STREAM_CONFIG_FETCH_ATTEMPT_INTERVAL);
        TIMER.schedule(eventSubmissionTask != null ? eventSubmissionTask : new EventSubmissionTask(), SEND_INTERVAL, SEND_INTERVAL);
    }

    /**
     * Periodic task that sends enqueued events if stream configs are present.
     * <p>
     * Visible for testing.
     */
    class EventSubmissionTask extends TimerTask {
        @Override
        public void run() {
            sendEnqueuedEvents();
        }
    }

    /**
     * Periodic task that attempts to fetch stream configs if they are not already present.
     * <p>
     * Visible for testing.
     */
    class FetchStreamConfigsTask extends TimerTask {
        @Override
        public void run() {
            if (streamConfigsReference.get().isEmpty()) {
                fetchStreamConfigs();
            }
        }
    }

}
