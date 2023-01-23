package org.wikimedia.metrics_platform.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.Event;
import org.wikimedia.metrics_platform.TestClientMetadata;
import org.wikimedia.metrics_platform.config.SourceConfigFixtures;
import org.wikimedia.metrics_platform.config.StreamConfig;

class ContextControllerTest {

    @Test void testAddRequestedValues() {
        ContextController contextController = new ContextController(new TestClientMetadata());
        Event event = new Event("test/event", "test.stream", "testEvent");
        StreamConfig streamConfig = SourceConfigFixtures.STREAM_CONFIGS_WITH_EVENTS.get("test.stream");
        contextController.addRequestedValues(event, streamConfig);

        AgentData agentData = event.getAgentData();
        PageData pageData = event.getPageData();
        PerformerData performerData = event.getPerformerData();
        DeviceData deviceData = event.getDeviceData();

        assertThat(agentData.getAppInstallId()).isEqualTo("6f31a4fa-0a77-4c65-9994-f242fa58ce94");
        assertThat(agentData.getClientPlatform()).isEqualTo("android");
        assertThat(agentData.getClientPlatformFamily()).isEqualTo("app");

        assertThat(pageData.getId()).isEqualTo(1);
        assertThat(pageData.getNamespaceId()).isEqualTo(0);
        assertThat(pageData.getNamespaceText()).isEmpty();
        assertThat(pageData.getTitle()).isEqualTo("Test");
        assertThat(pageData.getIsRedirect()).isFalse();
        assertThat(pageData.getRevisionId()).isEqualTo(1);
        assertThat(pageData.getContentLanguage()).isEqualTo("zh");
        assertThat(pageData.getWikidataItemId()).isEqualTo("Q1");
        assertThat(pageData.getGroupsAllowedToEdit()).isEmpty();
        assertThat(pageData.getGroupsAllowedToMove()).isEmpty();

        assertThat(performerData.getId()).isEqualTo(1);
        assertThat(performerData.getIsLoggedIn()).isTrue();
        assertThat(performerData.getName()).isEqualTo("TestUser");
        assertThat(performerData.getGroups()).containsExactly("*");
        assertThat(performerData.getEditCount()).isEqualTo(10);
        assertThat(performerData.getEditCountBucket()).isEqualTo("5-99 edits");
        assertThat(performerData.getRegistrationDt()).isEqualTo(1427224089000L);
        assertThat(performerData.getLanguage()).isEqualTo("zh");
        assertThat(performerData.getLanguageVariant()).isEqualTo("zh-tw");
        assertThat(performerData.getIsBot()).isFalse();
        assertThat(performerData.getCanProbablyEditPage()).isTrue();

        assertThat(deviceData.getPixelRatio()).isEqualTo(1.0f);
        assertThat(deviceData.getHardwareConcurrency()).isEqualTo(1);
        assertThat(deviceData.getMaxTouchPoints()).isEqualTo(1);

        assertThat(event.getAccessMethod()).isEqualTo("mobile app");
        assertThat(event.getIsProduction()).isTrue();
    }
}
