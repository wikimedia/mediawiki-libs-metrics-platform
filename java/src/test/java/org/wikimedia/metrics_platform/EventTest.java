package org.wikimedia.metrics_platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.wikimedia.metrics_platform.MetricsClient.DATE_FORMAT;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

class EventTest {

    @Test void testEvent() {
        Event event = new Event("test/event/1.0.0", "test.event", "testEvent");
        String timestamp = DATE_FORMAT.format(Instant.EPOCH);
        event.setAppInstallId("foo");
        event.setAppSessionId("bar");
        event.setTimestamp(timestamp);

        assertThat(event.getStream()).isEqualTo("test.event");
        assertThat(event.getTimestamp()).isEqualTo("1970-01-01T00:00:00Z");
    }

    @Test void testEventSerialization() {
        String uuid = UUID.randomUUID().toString();
        Event event = new Event("test/event/1.0.0", "test.event", "testEvent");
        event.setAppInstallId(uuid);
        event.setAppSessionId(uuid);
        event.setTimestamp("2021-08-27T12:00:00Z");
        event.setAccessMethod("mobile app");
        event.setPlatform("android");
        event.setPlatformFamily("app");
        event.setIsProduction(true);

        assertThat(event.getStream()).isEqualTo("test.event");
        assertThat(event.getSchema()).isEqualTo("test/event/1.0.0");
        assertThat(event.getName()).isEqualTo("testEvent");
        assertThat(event.getAppInstallId()).isEqualTo(uuid);
        assertThat(event.getAppSessionId()).isEqualTo(uuid);
        assertThat(event.getTimestamp()).isEqualTo("2021-08-27T12:00:00Z");
        assertThat(event.getAccessMethod()).isEqualTo("mobile app");
        assertThat(event.getPlatform()).isEqualTo("android");
        assertThat(event.getPlatformFamily()).isEqualTo("app");
        assertThat(event.getIsProduction()).isTrue();

        Gson gson = new Gson();
        String json = gson.toJson(event);
        assertThat(json).isEqualTo(String.format(Locale.ROOT,
                "{" +
                            "\"$schema\":\"test/event/1.0.0\"," +
                            "\"meta\":{\"stream\":\"test.event\"}," +
                            "\"name\":\"testEvent\"," +
                            "\"dt\":\"2021-08-27T12:00:00Z\"," +
                            "\"app_install_id\":\"%s\"," +
                            "\"app_session_id\":\"%s\"," +
                            "\"page\":{}," +
                            "\"performer\":{}," +
                            "\"device\":{}," +
                            "\"access_method\":\"mobile app\"," +
                            "\"platform\":\"android\"," +
                            "\"platform_family\":\"app\"," +
                            "\"is_production\":true" +
                        "}", uuid, uuid));
    }

}
