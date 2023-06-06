package org.wikimedia.metrics_platform.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.json.GsonHelper;

import com.google.gson.Gson;

class PerformerDataTest {

    @Test void testPerformerData() {
        PerformerData performerData = PerformerData.builder()
                .id(1)
                .name("TestPerformer")
                .isLoggedIn(true)
                .sessionId("eeeeeeeeeeeeeeeeeeee")
                .pageviewId("eeeeeeeeeeeeeeeeeeee")
                .groups(Collections.singletonList("*"))
                .isBot(false)
                .language("zh")
                .languageVariant("zh-tw")
                .canProbablyEditPage(true)
                .editCount(10)
                .editCountBucket("5-99 edits")
                .registrationDt(Instant.parse("2023-03-01T01:08:30Z"))
                .build();

        assertThat(performerData.getId()).isEqualTo(1);
        assertThat(performerData.getName()).isEqualTo("TestPerformer");
        assertThat(performerData.getIsLoggedIn()).isTrue();
        assertThat(performerData.getGroups()).isEqualTo(Collections.singletonList("*"));
        assertThat(performerData.getIsBot()).isFalse();
        assertThat(performerData.getLanguage()).isEqualTo("zh");
        assertThat(performerData.getLanguageVariant()).isEqualTo("zh-tw");
        assertThat(performerData.getCanProbablyEditPage()).isTrue();
        assertThat(performerData.getEditCount()).isEqualTo(10);
        assertThat(performerData.getEditCountBucket()).isEqualTo("5-99 edits");
        assertThat(performerData.getRegistrationDt()).isEqualTo("2023-03-01T01:08:30Z");

        Gson gson = GsonHelper.getGson();

        String json = gson.toJson(performerData);
        assertThat(json).isEqualTo("{\"name\":\"TestPerformer\"," +
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
                "}");
    }

}
