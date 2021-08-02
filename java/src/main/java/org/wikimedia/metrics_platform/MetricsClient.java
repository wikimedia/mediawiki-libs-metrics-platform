package org.wikimedia.metrics_platform;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MetricsClient {

    public static MetricsClient getInstance(MetricsClientIntegration integration) {
        return new MetricsClient(integration);
    }

    /**
     * Stream configs to be fetched on startup and stored for the duration of the app lifecycle.
     */
    private Map<String, StreamConfig> streamConfigs;

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
     * The input buffer is used to store unvalidated events when stream configurations are not yet
     * available. Because we cannot yet validate events, we cap the size at MAX_INPUT_BUFFER_SIZE.
     *
     * If the input buffer reaches its maximum capacity, events are dropped in the order in which
     * they were added.
     *
     * After stream configs are fetched, the input buffer contents are validated and moved to the
     * output buffer for submission, and the input buffer is no longer used.
     */
    private final Queue<Event> inputBuffer;

    /**
     * The output buffer holds validated events for submission every WAIT_MS ms.
     * When the event submission interval passes, all scheduled events are submitted together in a
     * single "burst" per destination event service.
     * TODO: Reevaluate thread synchronization
     */
    private final LinkedList<Event> outputBuffer;
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
        if (streamConfigs == null) {
            inputBuffer.add(event);
        } else if (shouldProcessEventsForStream(stream)) {
            outputBuffer.add(event);
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
    void setStreamConfigs(Map<String, StreamConfig> streamConfigs) {
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
    void moveInputBufferEventsToOutputBuffer() {
        while (!inputBuffer.isEmpty()) {
            Event event = inputBuffer.remove();
            String stream = event.getStream();
            if (shouldProcessEventsForStream(stream)) {
                outputBuffer.add(event);
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
        event.setSessionId(sessionController.getSessionId());
        event.setTimestamp(DATE_FORMAT.format(new Date()));
    }

    /**
     * Get events by stream from the output buffer and send them. If the request fails, the events
     * are placed back into the output buffer for retry.
     */
    private void sendEnqueuedEvents() {
        LinkedList<Event> eventsToSend = (LinkedList<Event>)outputBuffer.clone(); // shallow copy
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
    private MetricsClient(MetricsClientIntegration integration) {
        this(integration, new SessionController());
    }

    /**
     * @param integration integration
     * @param sessionController session controller
     */
    private MetricsClient(MetricsClientIntegration integration, SessionController sessionController) {
        this(
                integration,
                sessionController,
                new SamplingController(integration, sessionController),
                new CircularFifoQueue<>(128),
                new LinkedList<>()
        );
    }

    /**
     * Constructor for testing.
     *
     * @param integration integration
     * @param sessionController session controller
     * @param samplingController sampling controller
     * @param inputBuffer buffer for unverified events prior to stream configs being fetched
     * @param outputBuffer buffer for verified events pending submission to event platform intake
     */
    MetricsClient(
            MetricsClientIntegration integration,
            SessionController sessionController,
            SamplingController samplingController,
            Queue<Event> inputBuffer,
            LinkedList<Event> outputBuffer
    ) {
        this.integration = integration;
        this.sessionController = sessionController;
        this.samplingController = samplingController;
        this.inputBuffer = inputBuffer;
        this.outputBuffer = outputBuffer;

        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        TIMER.scheduleAtFixedRate(new EventSubmissionTask(), SEND_INTERVAL, SEND_INTERVAL);

        fetchStreamConfigs();
    }

    /**
     * Periodic task that sends enqueued events if stream configs are present. If not, it
     * attempts to fetch them so that events can be submitted on the next run.
     *
     * Visible for testing.
     */
    class EventSubmissionTask extends TimerTask {
        @Override
        public void run() {
            if (streamConfigs != null) {
                sendEnqueuedEvents();
            } else {
                fetchStreamConfigs();
            }
        }
    }

}
