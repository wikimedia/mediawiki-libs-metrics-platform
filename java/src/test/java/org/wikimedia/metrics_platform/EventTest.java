package org.wikimedia.metrics_platform;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.wikimedia.metrics_platform.MetricsClient.DATE_FORMAT;

public class EventTest {

    @Test
    public void testEvent() {
        Event event = new Event("test/event/1.0.0", "test.event");
        String timestamp = DATE_FORMAT.format(new Date());
        event.setAppInstallId("foo");
        event.setSessionId("bar");
        event.setTimestamp(timestamp);

        assertThat(event.getStream(), is("test.event"));
    }

}
