package org.wikimedia.metrics_platform;

import static java.time.Instant.now;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.wikimedia.metrics_platform.context.ContextController;
import org.wikimedia.metrics_platform.context.CustomData;
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

    private final AtomicReference<Map<String, Set<String>>> eventsStreamConfigsMap = new AtomicReference<>(emptyMap());

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

    private static final String METRICS_PLATFORM_SCHEMA = "/analytics/mediawiki/client/metrics_event/1.1.0";


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
     * Construct and submits a Metrics Platform Event from the event name and custom data for each
     * stream that is interested in those events.
     * <p>
     * The Metrics Platform Event for a stream (S) is constructed by: first initializing the minimum
     * valid event (E) that can be submitted to S; and, second mixing the context attributes requested
     * in the configuration for S into E.
     * <p>
     * The Metrics Platform Event is submitted to a stream (S) if: 1) S is in sample; and 2) the event
     * is filtered due to the filtering rules for S.
     * <p>
     * This particular dispatch method accepts unformatted custom data and calls the following dispatch
     * method with the custom data properly formatted.
     * <p>
     * @see <a href="https://wikitech.wikimedia.org/wiki/Metrics_Platform">Metrics Platform</a>
     *
     * @param eventName event name
     * @param customData custom data
     */
    public void dispatch(String eventName, Map<String, Object> customData) {
        Set<CustomData> customDataSetFormatted = customData.entrySet().stream()
            .map(CustomData::of)
            .collect(toSet());
        dispatch(eventName, customDataSetFormatted);
    }

    /**
     * See doc comment for above dispatch method.
     * <p>
     * This particular dispatch method accepts formatted custom data and submits the event.
     *
     * @param eventName event name
     * @param customData custom data
     */
    public void dispatch(String eventName, Set<CustomData> customData) {
        Set<String> streamNames = getStreamNamesForEvent(eventName);
        // Loop through stream configs to add event to pending events.
        if (streamNames != null) {
            for (String streamName : streamNames) {
                if (shouldProcessEventsForStream(streamName)) {
                    Event event = new Event(METRICS_PLATFORM_SCHEMA, streamName, eventName);
                    event.setCustomData(customData);
                    submit(event);
                }
            }
        }
    }

    /**
     * Return the event name to associated stream names map.
     *
     * @param streamConfigs stream configs
     * @return the event name to stream names map
     */
    private Map<String, Set<String>> getEventNameToStreamNamesMap(Map<String, StreamConfig> streamConfigs) {
        Map<String, Set<String>> eventsMap = new HashMap<>();
        for (Map.Entry<String, StreamConfig> entry : streamConfigs.entrySet()) {
            String streamName = entry.getValue().getStreamName();
            StreamConfig streamConfig = entry.getValue();
            if (!streamConfig.hasEvents()) {
                continue;
            }
            Set<String> events = streamConfig.getEvents();
            for (String event : events) {
                Set<String> streamNamesSet = eventsMap.get(event);
                if (streamNamesSet != null) {
                    streamNamesSet.add(streamName);
                } else {
                    Set<String> streamNames = new HashSet<>();
                    streamNames.add(streamName);
                    eventsMap.put(event, streamNames);
                }
            }
        }
        return eventsMap;
    }

    /**
     * Return the stream names for an event name.
     *
     * @param eventName event name
     * @return the collection of stream names for an event
     */
    private Set<String> getStreamNamesForEvent(String eventName) {
        Map<String, Set<String>> eventsStreamsMap = this.eventsStreamConfigsMap.get();
        return eventsStreamsMap.get(eventName);
    }

    /**
     * Convenience method to be called when
     * <a href="https://developer.android.com/guide/components/activities/activity-lifecycle#onpause">
     * the onPause() activity lifecycle callback</a> is called.
     * <p>
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
        this.eventsStreamConfigsMap.set(getEventNameToStreamNamesMap(this.streamConfigsReference.get()));
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

    // @VisibleForTesting
    boolean isPendingEventsEmpty() {
        return pendingEvents.isEmpty();
    }

    /**
     * MetricsClient Constructor.
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
            @Nullable TimerTask fetchStreamConfigsTask,
            @Nullable TimerTask eventSubmissionTask,
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
