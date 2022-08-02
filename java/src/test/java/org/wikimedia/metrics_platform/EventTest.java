package org.wikimedia.metrics_platform;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.wikimedia.metrics_platform.MetricsClient.DATE_FORMAT;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

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
        event.setAccessMethod("mobile app");
        event.setPlatform("android");
        event.setPlatformFamily("app");
        event.setIsProduction(true);

        assertThat(event.getStream(), is("test.event"));
        assertThat(event.getSchema(), is("test/event/1.0.0"));
        assertThat(event.getAppInstallId(), is(uuid));
        assertThat(event.getAppSessionId(), is(uuid));
        assertThat(event.getTimestamp(), is("2021-08-27T12:00:00Z"));
        assertThat(event.getAccessMethod(), is("mobile app"));
        assertThat(event.getPlatform(), is("android"));
        assertThat(event.getPlatformFamily(), is("app"));
        assertThat(event.isProduction(), is(true));

        Gson gson = new Gson();
        String json = gson.toJson(event);
        assertThat(json, is(String.format(Locale.ROOT, "{\"$schema\":\"test/event/1.0.0\",\"meta\":{\"stream\":\"test.event\"}," +
                "\"dt\":\"2021-08-27T12:00:00Z\",\"app_install_id\":\"%s\",\"app_session_id\":\"%s\"," +
                "\"access_method\":\"mobile app\",\"platform\":\"android\",\"platform_family\":\"app\"," +
                "\"is_production\":true}", uuid, uuid)));
    }

}
