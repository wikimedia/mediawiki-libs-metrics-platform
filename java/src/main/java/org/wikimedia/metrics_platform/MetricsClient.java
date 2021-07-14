package org.wikimedia.metrics_platform;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.wikimedia.metrics_platform.context.ContextController;
import org.wikimedia.metrics_platform.curation.CurationController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public final class MetricsClient {

    public static MetricsClient getInstance(MetricsClientIntegration integration) {
        return new MetricsClient(integration);
    }

    /**
     * Stream configs to be fetched on startup and stored for the duration of the app lifecycle.
     */
    private Map<String, StreamConfig> streamConfigs = Collections.emptyMap();
    private static final int STREAM_CONFIG_FETCH_ATTEMPT_INTERVAL = 30000; // 30 seconds

    /**
     * Integration layer exposing hosting application functionality to the client library.
     */
    private final MetricsClientIntegration integration;

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

    /**
     * The input buffer is used to store unvalidated events when stream configurations are not yet
     * available. Because we cannot yet validate events, we cap the size at MAX_INPUT_BUFFER_SIZE.
     *
     * If the input buffer reaches its maximum capacity, events are dropped in the order in which
     * they were added.
     *
     * After stream configs are fetched, the input buffer contents are validated and moved to the
     * output buffer for submission, and the input buffer is no longer used.
     */
    final Queue<Event> inputBuffer;

    /**
     * The output buffer holds validated events for submission every WAIT_MS ms.
     * When the event submission interval passes, all scheduled events are submitted together in a
     * single "burst" per destination event service.
     */
    final ArrayList<Event> outputBuffer;
    private static final int SEND_INTERVAL = 30000; // 30 seconds


    static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT);
    private static final Timer TIMER = new Timer();

    /**
     * Submit an event to be enqueued and sent to the Event Platform.
     *
     * If stream configs are not yet fetched, the event will be held temporarily in the input
     * buffer (provided there is space to do so).
     *
     * If stream configs are available, the event will be validated and enqueued for submission
     * to the configured event platform intake service.
     *
     * Supplemental metadata is added immediately on intake, regardless of the presence or absence
     * of stream configs, so that the event timestamp is recorded accurately.
     *
     * @param event event data
     * @param stream stream name
     */
    public synchronized void submit(Event event, String stream) {
        addRequiredMetadata(event);
        if (streamConfigs.isEmpty()) {
            inputBuffer.add(event);
        } else if (shouldProcessEventsForStream(stream)) {
            StreamConfig streamConfig = streamConfigs.get(stream);
            contextController.addRequestedValues(event, streamConfig);
            if (curationController.eventPassesCurationRules(event, streamConfig)) {
                outputBuffer.add(event);
            }
        }
    }

    /**
     * Convenience method to be called when an Android Application activity reaches the onPause()
     * stage of its lifecycle. Updates the session-touched timestamp so that we can determine
     * whether the session has expired if and when the application is resumed.
     */
    public void onApplicationPause() {
        sessionController.touchSession();
    }

    /**
     * Convenience method to be called when an Android Application activity reaches the onResume()
     * stage of its lifecycle. Resets the session if it has expired since the application was paused.
     */
    public void onApplicationResume() {
        if (sessionController.sessionExpired()) {
            sessionController.beginNewSession();
        } else {
            sessionController.touchSession();
        }
    }

    /**
     * Setter exposed for testing.
     *
     * @param streamConfigs stream configs
     */
    synchronized void setStreamConfigs(Map<String, StreamConfig> streamConfigs) {
        this.streamConfigs = streamConfigs;
    }

    /**
     * Returns true if the specified stream is configured and in sample.
     *
     * Visible for testing.
     *
     * @param stream stream name
     * @return boolean
     */
    boolean shouldProcessEventsForStream(String stream) {
        return streamConfigs.containsKey(stream) &&
                samplingController.isInSample(streamConfigs.get(stream));
    }

    /**
     * Validate events in the input buffer, add metadata to valid events, and move them to the output buffer.
     *
     * Visible for testing.
     */
    synchronized void moveInputBufferEventsToOutputBuffer() {
        while (!inputBuffer.isEmpty()) {
            Event event = inputBuffer.remove();
            String stream = event.getStream();
            if (shouldProcessEventsForStream(stream)) {
                StreamConfig streamConfig = streamConfigs.get(stream);
                contextController.addRequestedValues(event, streamConfig);
                if (curationController.eventPassesCurationRules(event, streamConfig)) {
                    outputBuffer.add(event);
                }
            }
        }
    }

    /**
     * Fetch stream configs and hold them in memory. After stream configs are fetched, move any
     * events in the input buffer over to the output buffer.
     */
    private void fetchStreamConfigs() {
        integration.fetchStreamConfigs(new MetricsClientIntegration.FetchStreamConfigsCallback() {
            @Override
            public void onSuccess(Map<String, StreamConfig> fetchedStreamConfigs) {
                setStreamConfigs(fetchedStreamConfigs);
                moveInputBufferEventsToOutputBuffer();
            }

            @Override
            public void onFailure() {
            }
        });
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
        event.setAppInstallId(integration.getAppInstallId());
        event.setAppSessionId(sessionController.getSessionId());
        event.setTimestamp(DATE_FORMAT.format(new Date()));
    }

    /**
     * Send all events currently in the output buffer.
     *
     * A shallow clone of the output buffer is created and passed to the integration layer for
     * submission by the client. If the event submission succeeds, the events are removed from the
     * output buffer. (Note that the shallow copy created by clone() retains pointers to the original
     * Event objects.) If the event submission fails, a client error is produced, and the events remain
     * in buffer to be retried on the next submission attempt.
     *
     * TODO: Add client error logging.
     */
    void sendEnqueuedEvents() {
        List<Event> eventsToSend = (ArrayList<Event>)outputBuffer.clone(); // shallow copy
        integration.sendEvents(DestinationEventService.ANALYTICS.getBaseUri(), eventsToSend,
                new MetricsClientIntegration.SendEventsCallback() {
                    @Override
                    public void onSuccess() {
                        // Sending succeeded; remove sent events from the output buffer.
                        for (Event event : eventsToSend) {
                            outputBuffer.remove(event);
                        }
                    }

                    @Override
                    public void onFailure() {
                        // Sending failed, events remain in output buffer :'(
                        // TODO: Verify that this is what we want to happen, ensure all libraries are in sync
                    }
                });
    }

    /**
     * MetricsClient Constructor.
     * @param integration integration implementation
     */
    MetricsClient(MetricsClientIntegration integration) {
        this(integration, new SessionController());
    }

    /**
     * @param integration integration
     * @param sessionController session controller
     */
    MetricsClient(MetricsClientIntegration integration, SessionController sessionController) {
        this(
                integration,
                sessionController,
                new SamplingController(integration, sessionController),
                new ContextController(integration)
        );
    }

    private MetricsClient(
            MetricsClientIntegration integration,
            SessionController sessionController,
            SamplingController samplingController,
            ContextController contextController
    ) {
        this(
                integration,
                sessionController,
                samplingController,
                contextController,
                new CurationController(),
                new CircularFifoQueue<>(128),
                new ArrayList<>(),
                null,
                null
        );
    }

    /**
     * Constructor for testing.
     *
     * @param integration integration
     * @param sessionController session controller
     * @param samplingController sampling controller
     * @param contextController context controller
     * @param curationController curation controller
     * @param inputBuffer buffer for unverified events prior to stream configs being fetched
     * @param outputBuffer buffer for verified events pending submission to event platform intake
     */
    MetricsClient(
            MetricsClientIntegration integration,
            SessionController sessionController,
            SamplingController samplingController,
            ContextController contextController,
            CurationController curationController,
            Queue<Event> inputBuffer,
            ArrayList<Event> outputBuffer,
            TimerTask fetchStreamConfigsTask,
            TimerTask eventSubmissionTask
    ) {
        this.integration = integration;
        this.sessionController = sessionController;
        this.samplingController = samplingController;
        this.contextController = contextController;
        this.curationController = curationController;
        this.inputBuffer = inputBuffer;
        this.outputBuffer = outputBuffer;

        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        TIMER.schedule(fetchStreamConfigsTask != null ? fetchStreamConfigsTask : new FetchStreamConfigsTask(), 0, STREAM_CONFIG_FETCH_ATTEMPT_INTERVAL);
        TIMER.schedule(eventSubmissionTask != null ? eventSubmissionTask : new EventSubmissionTask(), SEND_INTERVAL, SEND_INTERVAL);
    }

    /**
     * Periodic task that sends enqueued events if stream configs are present.
     *
     * Visible for testing.
     */
    class EventSubmissionTask extends TimerTask {
        @Override
        public void run() {
            if (!streamConfigs.isEmpty()) {
                sendEnqueuedEvents();
            }
        }
    }

    /**
     * Periodic task that attempts to fetch stream configs if they are not already present.
     *
     * Visible for testing.
     */
    class FetchStreamConfigsTask extends TimerTask {
        @Override
        public void run() {
            if (streamConfigs.isEmpty()) {
                fetchStreamConfigs();
            }
        }
    }

}
