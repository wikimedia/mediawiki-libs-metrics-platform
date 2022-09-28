package org.wikimedia.metrics_platform;

import java.io.IOException;
import java.util.Collection;

public interface EventSender {
    void sendEvents(String baseUri, Collection<Event> events) throws IOException;
}
