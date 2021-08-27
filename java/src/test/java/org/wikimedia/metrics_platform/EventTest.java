package org.wikimedia.metrics_platform;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.wikimedia.metrics_platform.MetricsClient.DATE_FORMAT;

public class EventTest {

    @Test
    public void testEvent() {
        Event event = new Event("test/event/1.0.0", "test.event");
        String timestamp = DATE_FORMAT.format(new Date());
        event.setAppInstallId("foo");
        event.setAppSessionId("bar");
        event.setTimestamp(timestamp);

        assertThat(event.getStream(), is("test.event"));
    }

    @Test
    public void testEventSerialization() {
        String uuid = UUID.randomUUID().toString();
        Event event = new Event("test/event/1.0.0", "test.event");
        event.setAppInstallId(uuid);
        event.setAppSessionId(uuid);
        event.setTimestamp("2021-08-27T12:00:00Z");

        assertThat(event.getStream(), is("test.event"));
        assertThat(event.getSchema(), is("test/event/1.0.0"));
        assertThat(event.getAppInstallId(), is(uuid));
        assertThat(event.getAppSessionId(), is(uuid));
        assertThat(event.getTimestamp(), is("2021-08-27T12:00:00Z"));

        Gson gson = new Gson();
        String json = gson.toJson(event);
        assertThat(json, is(String.format("{\"$schema\":\"test/event/1.0.0\",\"meta\":{\"stream\":\"test.event\"}," +
                "\"dt\":\"2021-08-27T12:00:00Z\",\"app_install_id\":\"%s\",\"app_session_id\":\"%s\"}", uuid, uuid)));
    }

}
