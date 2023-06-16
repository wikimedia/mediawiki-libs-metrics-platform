package org.wikimedia.metrics_platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.wikimedia.metrics_platform.MetricsClient.DATE_FORMAT;
import static org.wikimedia.metrics_platform.event.EventProcessed.fromEvent;

import java.time.Instant;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.context.DataFixtures;
import org.wikimedia.metrics_platform.event.Event;
import org.wikimedia.metrics_platform.event.EventProcessed;
import org.wikimedia.metrics_platform.json.GsonHelper;

import com.google.gson.Gson;

class EventTest {

    @Test void testEvent() {
        Event eventBasic = new Event("test/event/1.0.0", "test.event", "testEvent");
        EventProcessed event = fromEvent(eventBasic);
        String timestamp = DATE_FORMAT.format(Instant.EPOCH);
        event.getAgentData().setAppInstallId("foo");
        event.setTimestamp(timestamp);

        assertThat(event.getStream()).isEqualTo("test.event");
        assertThat(event.getTimestamp()).isEqualTo("1970-01-01T00:00:00Z");
    }

    @Test void testEventSerialization() {
        String uuid = UUID.randomUUID().toString();
        Event eventBasic = new Event("test/event/1.0.0", "test.event", "testEvent");
        EventProcessed event = fromEvent(eventBasic);

        event.setTimestamp("2021-08-27T12:00:00Z");

        event.setClientData(DataFixtures.getTestClientData());

        event.getAgentData().setAppInstallId(uuid);

        assertThat(event.getStream()).isEqualTo("test.event");
        assertThat(event.getSchema()).isEqualTo("test/event/1.0.0");
        assertThat(event.getName()).isEqualTo("testEvent");
        assertThat(event.getAgentData().getAppInstallId()).isEqualTo(uuid);
        assertThat(event.getTimestamp()).isEqualTo("2021-08-27T12:00:00Z");
        assertThat(event.getAgentData().getClientPlatform()).isEqualTo("android");
        assertThat(event.getAgentData().getClientPlatformFamily()).isEqualTo("app");

        assertThat(event.getPageData().getId()).isEqualTo(1);
        assertThat(event.getPageData().getTitle()).isEqualTo("Test Page Title");
        assertThat(event.getPageData().getNamespace()).isEqualTo(0);
        assertThat(event.getPageData().getNamespaceName()).isEqualTo("Main");
        assertThat(event.getPageData().getRevisionId()).isEqualTo(1);
        assertThat(event.getPageData().getWikidataItemQid()).isEqualTo("Q123456");
        assertThat(event.getPageData().getContentLanguage()).isEqualTo("en");
        assertThat(event.getPageData().getIsRedirect()).isFalse();
        assertThat(event.getPageData().getGroupsAllowedToMove()).contains("*");
        assertThat(event.getPageData().getGroupsAllowedToEdit()).contains("*");

        assertThat(event.getMediawikiData().getSkin()).isEqualTo("vector");
        assertThat(event.getMediawikiData().getVersion()).isEqualTo("1.40.0-wmf.20");
        assertThat(event.getMediawikiData().getIsProduction()).isTrue();
        assertThat(event.getMediawikiData().getIsDebugMode()).isFalse();
        assertThat(event.getMediawikiData().getDatabase()).isEqualTo("enwiki");
        assertThat(event.getMediawikiData().getSiteContentLanguage()).isEqualTo("en");
        assertThat(event.getMediawikiData().getSiteContentLanguageVariant()).isEqualTo("en-zh");

        assertThat(event.getPerformerData().getId()).isEqualTo(1);
        assertThat(event.getPerformerData().getName()).isEqualTo("TestPerformer");
        assertThat(event.getPerformerData().getIsLoggedIn()).isTrue();
        assertThat(event.getPerformerData().getSessionId()).isEqualTo("eeeeeeeeeeeeeeeeeeee");
        assertThat(event.getPerformerData().getPageviewId()).isEqualTo("eeeeeeeeeeeeeeeeeeee");
        assertThat(event.getPerformerData().getGroups()).isEqualTo(Collections.singletonList("*"));
        assertThat(event.getPerformerData().getIsBot()).isFalse();
        assertThat(event.getPerformerData().getLanguage()).isEqualTo("zh");
        assertThat(event.getPerformerData().getLanguageVariant()).isEqualTo("zh-tw");
        assertThat(event.getPerformerData().getCanProbablyEditPage()).isTrue();
        assertThat(event.getPerformerData().getEditCount()).isEqualTo(10);
        assertThat(event.getPerformerData().getRegistrationDt()).isEqualTo("2023-03-01T01:08:30Z");

        Gson gson = GsonHelper.getGson();
        String json = gson.toJson(event);
        assertThat(json).isEqualTo(String.format(Locale.ROOT,
                "{" +
                        "\"agent\":{" +
                        "\"app_install_id\":\"%s\"," +
                        "\"client_platform\":\"android\"," +
                        "\"client_platform_family\":\"app\"" +
                        "}," +
                        "\"page\":{" +
                        "\"id\":1," +
                        "\"title\":\"Test Page Title\"," +
                        "\"namespace\":0," +
                        "\"namespace_name\":\"Main\"," +
                        "\"revision_id\":1," +
                        "\"wikidata_qid\":\"Q123456\"," +
                        "\"content_language\":\"en\"," +
                        "\"is_redirect\":false," +
                        "\"user_groups_allowed_to_move\":[\"*\"]," +
                        "\"user_groups_allowed_to_edit\":[\"*\"]" +
                        "}," +
                        "\"mediawiki\":{" +
                        "\"skin\":\"vector\"," +
                        "\"version\":\"1.40.0-wmf.20\"," +
                        "\"is_production\":true," +
                        "\"is_debug_mode\":false," +
                        "\"database\":\"enwiki\"," +
                        "\"site_content_language\":\"en\"," +
                        "\"site_content_language_variant\":\"en-zh\"" +
                        "}," +
                        "\"performer\":{" +
                        "\"name\":\"TestPerformer\"," +
                        "\"is_logged_in\":true," +
                        "\"id\":1," +
                        "\"session_id\":\"eeeeeeeeeeeeeeeeeeee\"," +
                        "\"pageview_id\":\"eeeeeeeeeeeeeeeeeeee\"," +
                        "\"groups\":[\"*\"]," +
                        "\"is_bot\":false," +
                        "\"language\":\"zh\"," +
                        "\"language_variant\":\"zh-tw\"," +
                        "\"can_probably_edit_page\":true," +
                        "\"edit_count\":10," +
                        "\"edit_count_bucket\":\"5-99 edits\"," +
                        "\"registration_dt\":\"2023-03-01T01:08:30Z\"" +
                        "}," +
                        "\"$schema\":\"test/event/1.0.0\"," +
                        "\"name\":\"testEvent\"," +
                        "\"dt\":\"2021-08-27T12:00:00Z\"," +
                        "\"meta\":{\"stream\":\"test.event\"}" +
                        "}", uuid, uuid));
    }

}
