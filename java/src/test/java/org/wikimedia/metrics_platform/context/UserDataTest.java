package org.wikimedia.metrics_platform.context;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

public class UserDataTest {

    @Test
    public void testUserData() {
        UserData userData = new UserData.Builder()
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

        assertThat(userData.getId(), is(1));
        assertThat(userData.getName(), is("TestUser"));
        assertThat(userData.getGroups(), is(Collections.singletonList("*")));
        assertThat(userData.isLoggedIn(), is(true));
        assertThat(userData.isBot(), is(false));
        assertThat(userData.canProbablyEditPage(), is(true));
        assertThat(userData.getEditCount(), is(10));
        assertThat(userData.getEditCountBucket(), is("5-99 edits"));
        assertThat(userData.getRegistrationTimestamp(), is(1427224089000L));
        assertThat(userData.getLanguage(), is("zh"));
        assertThat(userData.getLanguageVariant(), is("zh-tw"));

        Gson gson = new Gson();
        String json = gson.toJson(userData);
        assertThat(json, is("{\"id\":1,\"name\":\"TestUser\",\"groups\":[\"*\"],\"is_logged_in\":true," +
                "\"is_bot\":false,\"can_probably_edit_page\":true,\"edit_count\":10," +
                "\"edit_count_bucket\":\"5-99 edits\",\"registration_timestamp\":1427224089000," +
                "\"language\":\"zh\",\"language_variant\":\"zh-tw\"}"));
    }

}
