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
        event.getAgentData().setAppInstallId("foo");
        event.setTimestamp(timestamp);

        assertThat(event.getStream()).isEqualTo("test.event");
        assertThat(event.getTimestamp()).isEqualTo("1970-01-01T00:00:00Z");
    }

    @Test void testEventSerialization() {
        String uuid = UUID.randomUUID().toString();
        Event event = new Event("test/event/1.0.0", "test.event", "testEvent");

        event.setTimestamp("2021-08-27T12:00:00Z");
        event.setAccessMethod("mobile app");

        event.getAgentData().setAppInstallId(uuid);
        event.getAgentData().setClientPlatform("android");
        event.getAgentData().setClientPlatformFamily("app");

        event.getMediawikiData().setSkin("vector");
        event.getMediawikiData().setVersion("1.40.0-wmf.19");
        event.getMediawikiData().setIsProduction(true);
        event.getMediawikiData().setIsDebugMode(false);
        event.getMediawikiData().setDatabase("enwiki");
        event.getMediawikiData().setSiteContentLanguage("en");
        event.getMediawikiData().setSiteContentLanguageVariant("en-zh");

        assertThat(event.getStream()).isEqualTo("test.event");
        assertThat(event.getSchema()).isEqualTo("test/event/1.0.0");
        assertThat(event.getName()).isEqualTo("testEvent");
        assertThat(event.getAgentData().getAppInstallId()).isEqualTo(uuid);
        assertThat(event.getTimestamp()).isEqualTo("2021-08-27T12:00:00Z");
        assertThat(event.getAccessMethod()).isEqualTo("mobile app");
        assertThat(event.getAgentData().getClientPlatform()).isEqualTo("android");
        assertThat(event.getAgentData().getClientPlatformFamily()).isEqualTo("app");

        Gson gson = new Gson();
        String json = gson.toJson(event);
        assertThat(json).isEqualTo(String.format(Locale.ROOT,
                "{" +
                            "\"$schema\":\"test/event/1.0.0\"," +
                            "\"meta\":{\"stream\":\"test.event\"}," +
                            "\"name\":\"testEvent\"," +
                            "\"dt\":\"2021-08-27T12:00:00Z\"," +
                            "\"agent\":{\"app_install_id\":\"%s\"," +
                            "\"client_platform\":\"android\"," +
                            "\"client_platform_family\":\"app\"}," +
                            "\"mediawiki\":{\"skin\":\"vector\"," +
                            "\"version\":\"1.40.0-wmf.19\"," +
                            "\"is_production\":true," +
                            "\"is_debug_mode\":false," +
                            "\"database\":\"enwiki\"," +
                            "\"site_content_language\":\"en\"," +
                            "\"site_content_language_variant\":\"en-zh\"}," +
                            "\"page\":{}," +
                            "\"performer\":{}," +
                            "\"device\":{}," +
                            "\"access_method\":\"mobile app\"" +
                        "}", uuid, uuid));
    }

}
