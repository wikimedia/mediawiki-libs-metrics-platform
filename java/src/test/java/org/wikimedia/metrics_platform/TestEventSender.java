package org.wikimedia.metrics_platform;

import java.io.IOException;
import java.util.Collection;

import org.wikimedia.metrics_platform.event.Event;

class TestEventSender implements EventSender {

    private final boolean shouldFail;

    TestEventSender() {
        this(false);
    }

    TestEventSender(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public void sendEvents(String baseUri, Collection<Event> events) throws IOException {
        if (shouldFail) {
            throw new IOException();
        }
    }
}
