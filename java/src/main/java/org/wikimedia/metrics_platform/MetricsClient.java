package org.wikimedia.metrics_platform;

import static java.time.Instant.now;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.WARNING;
import static java.util.stream.Collectors.toSet;
import static org.wikimedia.metrics_platform.config.StreamConfigFetcher.ANALYTICS_API_ENDPOINT;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.wikimedia.metrics_platform.config.SourceConfig;
import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.config.StreamConfigFetcher;
import org.wikimedia.metrics_platform.context.CustomData;
import org.wikimedia.metrics_platform.event.Event;

import lombok.extern.java.Log;

@Log
public final class MetricsClient {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT)
            .withZone(ZoneId.of("UTC"));

    private static final Duration STREAM_CONFIG_FETCH_ATTEMPT_INTERVAL = Duration.ofSeconds(30);

    private static final Duration SEND_INTERVAL = Duration.ofSeconds(30);

    private static final String METRICS_PLATFORM_SCHEMA = "/analytics/mediawiki/client/metrics_event/1.2.0";

    /**
     * Integration layer exposing hosting application functionality to the client library.
     */
    private final ClientMetadata clientMetadata;

    private final AtomicReference<SourceConfig> sourceConfig;

    /**
     * Handles logging session management. A new session begins (and a new session ID is created)
     * if the app has been inactive for 15 minutes or more.
     */
    private final SessionController sessionController;

    /**
     * Evaluates whether events for a given stream are in-sample based on the stream configuration.
     */
    private final SamplingController samplingController;

    private final BlockingQueue<Event> eventQueue;

    /**
     * MetricsClient constructor.
     */
    public MetricsClient(
            ClientMetadata clientMetadata,
            SessionController sessionController,
            SamplingController samplingController,
            AtomicReference<SourceConfig> sourceConfig,
            BlockingQueue<Event> eventQueue
    ) {
        this.clientMetadata = clientMetadata;
        this.sessionController = sessionController;
        this.samplingController = samplingController;
        this.sourceConfig = sourceConfig;
        this.eventQueue = eventQueue;
    }

    public static MetricsClient createMetricsClient(
        ClientMetadata clientMetadata,
        EventSender eventSender,
        int capacity
    ) throws MalformedURLException {
        StreamConfigFetcher streamConfigFetcher = new StreamConfigFetcher(new URL(ANALYTICS_API_ENDPOINT));
        SessionController sessionController = new SessionController();
        SamplingController samplingController = new SamplingController(clientMetadata, sessionController);
        ContextController contextController = new ContextController(clientMetadata);

        AtomicReference<SourceConfig> sourceConfigRef = new AtomicReference<>();
        BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>(capacity);

        EventProcessor eventProcessor = new EventProcessor(
                contextController,
                sourceConfigRef,
                eventSender,
                eventQueue
        );

        MetricsClient metricsClient = new MetricsClient(
                clientMetadata,
                sessionController,
                samplingController,
                sourceConfigRef,
                eventQueue
        );

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new SimpleThreadFactory());

        executorService.scheduleAtFixedRate(
            () -> {
                try {
                    SourceConfig sourceConfig = streamConfigFetcher.fetchStreamConfigs();
                    sourceConfigRef.set(sourceConfig);
                } catch (IOException e) {
                    log.log(WARNING, "Could not fetch configuration.", e);
                }
            },
            0, STREAM_CONFIG_FETCH_ATTEMPT_INTERVAL.toMillis(), MILLISECONDS);

        executorService.scheduleAtFixedRate(
                eventProcessor::sendEnqueuedEvents,
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
        if (!eventQueue.offer(event)) {
            log.log(FINE, "Event dropped, event queue is full");
        }
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
    public void onAppPause() {
        sessionController.touchSession();
    }

    /**
     * Convenience method to be called when
     * <a href="https://developer.android.com/guide/components/activities/activity-lifecycle#onresume">
     * the onResume() activity lifecycle callback</a> is called.
     * <p>
     * Touches the session so that we can determine whether it has expired.
     */
    public void onAppResume() {
        sessionController.touchSession();
    }

    /**
     * Closes the session.
     */
    public void onAppClose() {
        sessionController.closeSession();
    }

    /**
     * Begins a new session and touches the session.
     */
    public void resetSession() {
        sessionController.beginSession();
    }

    /**
     * Supplement the outgoing event with additional metadata.
     * These include:
     * - app_install_id: app install ID
     * - app_session_id: the current session ID
     * - dt: ISO 8601 timestamp
     * - domain: the
     *
     * @param event event
     */
    private void addRequiredMetadata(Event event) {
        event.getAgentData().setAppInstallId(clientMetadata.getAgentAppInstallId());
        event.getPerformerData().setSessionId(sessionController.getSessionId());
        event.setDomain(clientMetadata.getDomain());
        event.setTimestamp(DATE_FORMAT.format(now()));
    }

    /**
     * Returns true if the specified stream is configured and in sample.
     */
    private boolean shouldProcessEventsForStream(String streamName, SourceConfig sourceConfig) {
        StreamConfig streamConfig = sourceConfig.getStreamConfigByName(streamName);
        return streamConfig != null && samplingController.isInSample(streamConfig);
    }

    private static class SimpleThreadFactory implements ThreadFactory {

        private final AtomicLong counter = new AtomicLong();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread("metrics-client-" + counter.incrementAndGet());
        }
    }
}
