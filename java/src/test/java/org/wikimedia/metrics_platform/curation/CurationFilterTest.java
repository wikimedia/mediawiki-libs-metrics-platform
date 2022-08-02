package org.wikimedia.metrics_platform.curation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.Event;
import org.wikimedia.metrics_platform.context.DeviceData;
import org.wikimedia.metrics_platform.context.PageData;
import org.wikimedia.metrics_platform.context.UserData;

import com.google.gson.Gson;

public class CurationFilterTest {

    private static CurationFilter curationFilter;

    @BeforeAll
    static void setUp() {
        Gson gson = new Gson();
        String curationFilterJson = "{\"page_id\":{\"less_than\":500,\"not_equals\":42},\"page_namespace_text\":" +
                "{\"equals\":\"Talk\"},\"user_is_logged_in\":{\"equals\":true},\"user_edit_count_bucket\":" +
                "{\"in\":[\"100-999 edits\",\"1000+ edits\"]},\"user_groups\":{\"contains_all\":" +
                "[\"user\",\"autoconfirmed\"],\"does_not_contain\":\"sysop\"},\"device_pixel_ratio\":" +
                "{\"greater_than_or_equals\":1.5,\"less_than_or_equals\":2.5}}";
        curationFilter = gson.fromJson(curationFilterJson, CurationFilter.class);
    }

    private static Event getBaseEvent() {
        PageData pageData = new PageData.Builder().id(1).namespaceText("Talk").build();
        UserData userData = new UserData.Builder().groups(Arrays.asList("user", "autoconfirmed", "steward"))
                .isLoggedIn(true).editCountBucket("1000+ edits").build();
        DeviceData deviceData = new DeviceData.Builder().pixelRatio(2.0f).build();

        Event event = new Event("test/event", "test.event");
        event.setPageData(pageData);
        event.setUserData(userData);
        event.setDeviceData(deviceData);
        return event;
    }

    @Test
    public void testEventPasses() {
        assertThat(curationFilter.apply(getBaseEvent()), is(true));
    }

    @Test
    public void testEventFailsWrongPageId() {
        Event event = getBaseEvent();
        event.getPageData().setId(42);
        assertThat(curationFilter.apply(event), is(false));
    }

    @Test
    public void testEventFailsWrongPageNamespaceText() {
        Event event = getBaseEvent();
        event.getPageData().setNamespaceText("User");
        assertThat(curationFilter.apply(event), is(false));
    }

    @Test
    public void testEventFailsWrongUserGroups() {
        Event event = getBaseEvent();
        event.getUserData().setGroups(Arrays.asList("user", "autoconfirmed", "sysop"));
        assertThat(curationFilter.apply(event), is(false));
    }

    @Test
    public void testEventFailsNoUserGroups() {
        Event event = getBaseEvent();
        event.getUserData().setGroups(Collections.emptyList());
        assertThat(curationFilter.apply(event), is(false));
    }

    @Test
    public void testEventFailsNotLoggedIn() {
        Event event = getBaseEvent();
        event.getUserData().setIsLoggedIn(false);
        assertThat(curationFilter.apply(event), is(false));
    }

    @Test
    public void testEventFailsWrongUserEditCountBucket() {
        Event event = getBaseEvent();
        event.getUserData().setEditCountBucket("5-99 edits");
        assertThat(curationFilter.apply(event), is(false));
    }

    @Test
    public void testEventFailsDevicePixelRatioTooHigh() {
        Event event = getBaseEvent();
        event.getDeviceData().setPixelRatio(1.0f);
        assertThat(curationFilter.apply(event), is(false));
    }

    @Test
    public void testEventFailsDevicePixelRatioTooLow() {
        Event event = getBaseEvent();
        event.getDeviceData().setPixelRatio(3.0f);
        assertThat(curationFilter.apply(event), is(false));
    }

}
