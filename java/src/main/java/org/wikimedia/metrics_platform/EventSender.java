package org.wikimedia.metrics_platform;

import java.io.IOException;
import java.util.Collection;

public interface EventSender {

    /**
     * Transmit an event to a destination intake service.
     *
     * @param baseUri base uri of destination intake service
     * @param events events to be sent
     */
    void sendEvents(String baseUri, Collection<Event> events) throws IOException;
}
