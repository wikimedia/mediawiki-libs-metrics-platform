package org.wikimedia.metrics_platform.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.Event;
import org.wikimedia.metrics_platform.config.SourceConfigFixtures;
import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.TestClientMetadata;

public class ContextControllerTest {

    @Test
    public void testAddRequestedValues() {
        ContextController contextController = new ContextController(new TestClientMetadata());
        Event event = new Event("test/event", "test.stream", "testEvent");
        StreamConfig streamConfig = SourceConfigFixtures.STREAM_CONFIGS_WITH_EVENTS.get("test.stream");
        contextController.addRequestedValues(event, streamConfig);

        PageData pageData = event.getPageData();
        UserData userData = event.getUserData();
        DeviceData deviceData = event.getDeviceData();

        assertThat(pageData.getId()).isEqualTo(1);
        assertThat(pageData.getNamespaceId()).isEqualTo(0);
        assertThat(pageData.getNamespaceText()).isEqualTo("");
        assertThat(pageData.getTitle()).isEqualTo("Test");
        assertThat(pageData.getIsRedirect()).isEqualTo(false);
        assertThat(pageData.getRevisionId()).isEqualTo(1);
        assertThat(pageData.getContentLanguage()).isEqualTo("zh");
        assertThat(pageData.getWikidataItemId()).isEqualTo("Q1");
        assertThat(pageData.getGroupsAllowedToEdit()).isEqualTo(Collections.emptySet());
        assertThat(pageData.getGroupsAllowedToMove()).isEqualTo(Collections.emptySet());

        assertThat(userData.getId()).isEqualTo(1);
        assertThat(userData.getIsLoggedIn()).isEqualTo(true);
        assertThat(userData.getName()).isEqualTo("TestUser");
        assertThat(userData.getGroups().toString()).isEqualTo("[*]");
        assertThat(userData.getEditCount()).isEqualTo(10);
        assertThat(userData.getEditCountBucket()).isEqualTo("5-99 edits");
        assertThat(userData.getRegistrationTimestamp()).isEqualTo(1427224089000L);
        assertThat(userData.getLanguage()).isEqualTo("zh");
        assertThat(userData.getLanguageVariant()).isEqualTo("zh-tw");
        assertThat(userData.getIsBot()).isEqualTo(false);
        assertThat(userData.getCanProbablyEditPage()).isEqualTo(true);

        assertThat(deviceData.getPixelRatio()).isEqualTo(1.0f);
        assertThat(deviceData.getHardwareConcurrency()).isEqualTo(1);
        assertThat(deviceData.getMaxTouchPoints()).isEqualTo(1);

        assertThat(event.getAccessMethod()).isEqualTo("mobile app");
        assertThat(event.getPlatform()).isEqualTo("android");
        assertThat(event.getPlatformFamily()).isEqualTo("app");
        assertThat(event.getIsProduction()).isEqualTo(true);
    }

}
