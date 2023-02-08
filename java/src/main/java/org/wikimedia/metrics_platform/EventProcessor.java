package org.wikimedia.metrics_platform;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import javax.annotation.concurrent.ThreadSafe;

import org.wikimedia.metrics_platform.config.CurationFilter;
import org.wikimedia.metrics_platform.config.DestinationEventService;
import org.wikimedia.metrics_platform.config.SourceConfig;
import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.event.Event;

import lombok.extern.java.Log;

@ThreadSafe
@Log
public class EventProcessor {

    /**
     * Enriches event data with context data requested in the stream configuration.
     */
    private final ContextController contextController;

    private final AtomicReference<SourceConfig> sourceConfig;
    private final BlockingQueue<Event> eventQueue;
    private final EventSender eventSender;


    /**
     * EventProcessor constructor.
     */
    public EventProcessor(
            ContextController contextController,
            CurationController curationController,
            AtomicReference<SourceConfig> sourceConfig,
            EventSender eventSender,
            BlockingQueue<Event> eventQueue
    ) {
        this.contextController = contextController;
        this.sourceConfig = sourceConfig;
        this.eventSender = eventSender;
        this.eventQueue = eventQueue;
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
        SourceConfig config = sourceConfig.get();
        if (config == null) {
            log.log(Level.FINE, "Configuration is missing, enqueued events are not sent.");
            return;
        }

        ArrayList<Event> pending = new ArrayList<>();
        this.eventQueue.drainTo(pending);

        Map<String, StreamConfig> streamConfigsMap = config.getStreamConfigsMap();

        pending.stream()
                .filter(event -> eventPassesCurationRules(event, streamConfigsMap))
                .collect(groupingBy(event -> destinationEventService(event, streamConfigsMap), toList()))
                .forEach(this::sendEventsToDestination);
    }

    protected boolean eventPassesCurationRules(Event event, Map<String, StreamConfig> streamConfigMap) {
        StreamConfig streamConfig = streamConfigMap.get(event.getStream());
        contextController.addRequestedValues(event, streamConfig);
        StreamConfig.ProducerConfig producerConfig = streamConfig.getProducerConfig();
        if (producerConfig == null) return true;

        StreamConfig.MetricsPlatformClientConfig metricsPlatformClientConfig = producerConfig.getMetricsPlatformClientConfig();
        if (metricsPlatformClientConfig == null) return true;

        CurationFilter curationFilter = metricsPlatformClientConfig.getCurationFilter();
        if (curationFilter == null) return true;

        return curationFilter.apply(event);
    }

    private DestinationEventService destinationEventService(Event event, Map<String, StreamConfig> streamConfigMap) {
        StreamConfig streamConfig = streamConfigMap.get(event.getStream());
        return streamConfig.getDestinationEventService();
    }

    private void sendEventsToDestination(
        DestinationEventService destinationEventService,
        List<Event> pendingValidEvents
    ) {
        try {
            eventSender.sendEvents(destinationEventService.getBaseUri(), pendingValidEvents);
        } catch (IOException ignore) {
            eventQueue.addAll(pendingValidEvents);
        }
    }
}
