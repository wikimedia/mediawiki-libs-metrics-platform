package org.wikimedia.metrics_platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EventBuffer {
    private final ArrayList<Event> events;
    private final DestinationEventService destination;

    EventBuffer(DestinationEventService destination) {
        this.events = new ArrayList<>();
        this.destination = destination;
    }

    DestinationEventService getDestinationService() {
        return destination;
    }

    boolean add(Event event) {
        return events.add(event);
    }

    boolean remove(Event event) {
        return events.remove(event);
    }

    boolean removeAll(Collection<Event> toRemove) {
        return events.removeAll(toRemove);
    }

    boolean isEmpty() {
        return events.isEmpty();
    }

    /**
     * Returns a shallow copy of the current list of events (i.e., a new list pointing
     * to the same events).
     *
     * @return events
     */
    List<Event> peekAll() {
        return (ArrayList<Event>)events.clone();
    }

}
