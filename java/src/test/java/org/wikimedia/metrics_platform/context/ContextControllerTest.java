package org.wikimedia.metrics_platform.context;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.Event;
import org.wikimedia.metrics_platform.StreamConfig;
import org.wikimedia.metrics_platform.TestMetricsClientIntegration;

public class ContextControllerTest {

    @Test
    public void testAddRequestedValues() {
        ContextController contextController = new ContextController(new TestMetricsClientIntegration());
        Event event = new Event("test/event", "test.event");
        StreamConfig streamConfig = TestMetricsClientIntegration.STREAM_CONFIGS.get("test.event");
        contextController.addRequestedValues(event, streamConfig);

        PageData pageData = event.getPageData();
        UserData userData = event.getUserData();
        DeviceData deviceData = event.getDeviceData();

        assertThat(pageData.getId(), is(1));
        assertThat(pageData.getNamespaceId(), is(0));
        assertThat(pageData.getNamespaceText(), is(""));
        assertThat(pageData.getTitle(), is("Test"));
        assertThat(pageData.isRedirect(), is(false));
        assertThat(pageData.getRevisionId(), is(1));
        assertThat(pageData.getContentLanguage(), is("zh"));
        assertThat(pageData.getWikidataItemId(), is("Q1"));
        assertThat(pageData.getGroupsAllowedToEdit(), is(Collections.emptySet()));
        assertThat(pageData.getGroupsAllowedToMove(), is(Collections.emptySet()));

        assertThat(userData.getId(), is(1));
        assertThat(userData.isLoggedIn(), is(true));
        assertThat(userData.getName(), is("TestUser"));
        assertThat(userData.getGroups(), is(singletonList("*")));
        assertThat(userData.getEditCount(), is(10));
        assertThat(userData.getEditCountBucket(), is("5-99 edits"));
        assertThat(userData.getRegistrationTimestamp(), is(1427224089000L));
        assertThat(userData.getLanguage(), is("zh"));
        assertThat(userData.getLanguageVariant(), is("zh-tw"));
        assertThat(userData.isBot(), is(false));
        assertThat(userData.canProbablyEditPage(), is(true));

        assertThat(deviceData.getPixelRatio(), is(1.0f));
        assertThat(deviceData.getHardwareConcurrency(), is(1));
        assertThat(deviceData.getMaxTouchPoints(), is(1));

        assertThat(event.getAccessMethod(), is("mobile app"));
        assertThat(event.getPlatform(), is("android"));
        assertThat(event.getPlatformFamily(), is("app"));
        assertThat(event.isProduction(), is(true));
    }

}
