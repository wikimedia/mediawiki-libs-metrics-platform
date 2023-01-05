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
        MediawikiData mediawikiData = event.getMediawikiData();
        PageData pageData = event.getPageData();
        PerformerData performerData = event.getPerformerData();

        assertThat(agentData.getAppInstallId()).isEqualTo("6f31a4fa-0a77-4c65-9994-f242fa58ce94");
        assertThat(agentData.getClientPlatform()).isEqualTo("android");
        assertThat(agentData.getClientPlatformFamily()).isEqualTo("app");

        assertThat(mediawikiData.getSkin()).isEqualTo("vector");
        assertThat(mediawikiData.getVersion()).isEqualTo("1.40.0-wmf.19");
        assertThat(mediawikiData.getIsProduction()).isEqualTo(true);
        assertThat(mediawikiData.getIsDebugMode()).isEqualTo(false);
        assertThat(mediawikiData.getDatabase()).isEqualTo("enwiki");
        assertThat(mediawikiData.getSiteContentLanguage()).isEqualTo("en");
        assertThat(mediawikiData.getSiteContentLanguageVariant()).isEqualTo("en-zh");

        assertThat(pageData.getId()).isEqualTo(1);
        assertThat(pageData.getNamespace()).isEqualTo(0);
        assertThat(pageData.getNamespaceName()).isEmpty();
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
    }
}
