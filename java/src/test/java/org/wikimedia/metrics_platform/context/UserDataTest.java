package org.wikimedia.metrics_platform.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

class UserDataTest {

    @Test void testUserData() {
        UserData userData = UserData.builder()
                .id(1)
                .name("TestUser")
                .groups(Collections.singletonList("*"))
                .isLoggedIn(true)
                .isBot(false)
                .canProbablyEditPage(true)
                .editCount(10)
                .editCountBucket("5-99 edits")
                .registrationTimestamp(1427224089000L)
                .language("zh")
                .languageVariant("zh-tw")
                .build();

        assertThat(userData.getId()).isEqualTo(1);
        assertThat(userData.getName()).isEqualTo("TestUser");
        assertThat(userData.getGroups()).isEqualTo(Collections.singletonList("*"));
        assertThat(userData.getIsLoggedIn()).isTrue();
        assertThat(userData.getIsBot()).isFalse();
        assertThat(userData.getCanProbablyEditPage()).isTrue();
        assertThat(userData.getEditCount()).isEqualTo(10);
        assertThat(userData.getEditCountBucket()).isEqualTo("5-99 edits");
        assertThat(userData.getRegistrationTimestamp()).isEqualTo(1427224089000L);
        assertThat(userData.getLanguage()).isEqualTo("zh");
        assertThat(userData.getLanguageVariant()).isEqualTo("zh-tw");

        Gson gson = new Gson();
        String json = gson.toJson(userData);
        assertThat(json).isEqualTo("{\"id\":1,\"name\":\"TestUser\",\"groups\":[\"*\"],\"is_logged_in\":true," +
                "\"is_bot\":false,\"can_probably_edit_page\":true,\"edit_count\":10," +
                "\"edit_count_bucket\":\"5-99 edits\",\"registration_timestamp\":1427224089000," +
                "\"language\":\"zh\",\"language_variant\":\"zh-tw\"}");
    }

}
