package org.wikimedia.metrics_platform;

import static java.time.Instant.now;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.logging.Level.WARNING;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.wikimedia.metrics_platform.config.SourceConfig;
import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.config.StreamConfigFetcher;
import org.wikimedia.metrics_platform.context.ContextController;
import org.wikimedia.metrics_platform.context.CustomData;
import org.wikimedia.metrics_platform.curation.CurationController;

import lombok.extern.java.Log;

@Log
public final class MetricsClient {

    private final BlockingQueue<Event> pendingEvents;

    private static final Duration STREAM_CONFIG_FETCH_ATTEMPT_INTERVAL = Duration.ofSeconds(30);

    private static final Duration SEND_INTERVAL = Duration.ofSeconds(30);

    /**
     * Integration layer exposing hosting application functionality to the client library.
     */
    private final ClientMetadata clientMetadata;

    /**
     * Visibility for testing purposed only.
     */
    final AtomicReference<SourceConfig> sourceConfig = new AtomicReference<>();

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


    private static final String METRICS_PLATFORM_SCHEMA = "/analytics/mediawiki/client/metrics_event";


    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT)
            .withZone(ZoneId.of("UTC"));

    public MetricsClient(
            ClientMetadata clientMetadata,
            EventSender eventSender,
            SessionController sessionController,
            SamplingController samplingController,
            ContextController contextController,
            CurationController curationController,
            SourceConfig sourceConfig, int capacity
    ) {
        this.clientMetadata = clientMetadata;
        this.sessionController = sessionController;
        this.samplingController = samplingController;
        this.contextController = contextController;
        this.curationController = curationController;
        this.pendingEvents = new LinkedBlockingQueue<>(capacity);
        this.eventSender = eventSender;
        this.sourceConfig.set(sourceConfig);
    }

    public static MetricsClient createMetricsClient(ClientMetadata clientMetadata, EventSender eventSender) throws MalformedURLException {

        StreamConfigFetcher streamConfigFetcher = new StreamConfigFetcher(new URL(StreamConfigFetcher.ANALYTICS_API_ENDPOINT));
        SessionController sessionController = new SessionController();
        SamplingController samplingController = new SamplingController(clientMetadata, sessionController);
        ContextController contextController = new ContextController(clientMetadata);
        CurationController curationController = new CurationController();

        MetricsClient metricsClient = new MetricsClient(
                clientMetadata,
                eventSender,
                sessionController,
                samplingController,
                contextController,
                curationController,
                null, 100
        );

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new SimpleThreadFactory());

        executorService.scheduleAtFixedRate(
            () -> {
                try {
                    metricsClient.setSourceConfig(streamConfigFetcher.fetchStreamConfigs());
                } catch (IOException e) {
                    log.log(WARNING, "Could not fetch configuration.", e);
                }
            },
            0, STREAM_CONFIG_FETCH_ATTEMPT_INTERVAL.toMillis(), MILLISECONDS);

        executorService.scheduleAtFixedRate(
                () -> metricsClient.sendEnqueuedEvents(),
                1, SEND_INTERVAL.toMillis(), MILLISECONDS);

        return metricsClient;
    }

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
        SourceConfig sourceConfig = this.sourceConfig.get();
        if (sourceConfig == null) {
            log.log(Level.FINE, "Configuration not loaded yet, the dispatched event is ignored and dropped.");
            return;
        }

        Set<String> streamNames = sourceConfig.getStreamNamesByEvent(eventName);
        // Loop through stream configs to add event to pending events.
        for (String streamName : streamNames) {
            if (shouldProcessEventsForStream(streamName, sourceConfig)) {
                Event event = new Event(METRICS_PLATFORM_SCHEMA, streamName, eventName);
                event.setCustomData(customData);
                submit(event);
            }
        }
    }

    /**
     * Convenience method to be called when
     * <a href="https://developer.android.com/guide/components/activities/activity-lifecycle#onpause">
     * the onPause() activity lifecycle callback</a> is called.
     * <p>
     * Touches the session so that we can determine whether it's session has expired if and when the
     * application is resumed.
     */
    public void onApplicationPause() {
        sessionController.touchSession();
    }

    /**
     * Convenience method to be called when
     * <a href="https://developer.android.com/guide/components/activities/activity-lifecycle#onresume">
     * the onResume() activity lifecycle callback</a> is called.
     * <p>
     * Touches the session so that we can determine whether it has expired.
     */
    public void onApplicationResume() {
        sessionController.touchSession();
    }

    /**
     * Returns true if the specified stream is configured and in sample.
     * <p>
     * Visible for testing.
     *
     * @param stream stream name
     * @param sourceConfig
     * @return boolean
     */
    // Todo: include condition in filter when sendingEnqueuedEvents
    boolean shouldProcessEventsForStream(String stream, SourceConfig sourceConfig) {
        StreamConfig streamConfig = sourceConfig.getStreamConfigByName(stream);
        return streamConfig != null && samplingController.isInSample(streamConfig);
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
     */
    public void sendEnqueuedEvents() {
        SourceConfig sourceConfig = this.sourceConfig.get();
        if (sourceConfig == null) {
            log.log(Level.FINE, "Configuration is missing, enqueued events are not sent.");
            return;
        }

        ArrayList<Event> pending = new ArrayList<>();
        this.pendingEvents.drainTo(pending);

        Map<String, StreamConfig> streamConfigsMap = sourceConfig.getStreamConfigsMap();

        pending.stream()
            .filter(event -> eventPassesCurationRules(event, streamConfigsMap))
            .collect(groupingBy(event -> destinationEventService(event, streamConfigsMap), toList()))
            .forEach(this::sendEventsToDestination);
    }

    private boolean eventPassesCurationRules(Event event, Map<String, StreamConfig> streamConfigMap) {
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

    // Visible for testing
    boolean hasPendingEvents() {
        return !pendingEvents.isEmpty();
    }

    public void setSourceConfig(SourceConfig sourceConfig) {
        this.sourceConfig.set(sourceConfig);
    }

    private static class SimpleThreadFactory implements ThreadFactory {

        private final AtomicLong counter = new AtomicLong();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread("metrics-client-" + counter.incrementAndGet());
        }
    }
}
