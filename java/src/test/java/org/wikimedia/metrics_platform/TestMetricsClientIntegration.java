package org.wikimedia.metrics_platform;

import static java.util.Collections.singletonList;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wikimedia.metrics_platform.curation.CurationFilter;
import org.wikimedia.metrics_platform.curation.rules.CollectionCurationRules;
import org.wikimedia.metrics_platform.curation.rules.CurationRules;

public class TestMetricsClientIntegration implements MetricsClientIntegration {

    public static final Map<String, StreamConfig> STREAM_CONFIGS = new HashMap<String, StreamConfig>() {{
        put("test.event", new StreamConfig(
                "test.event",
                "test/event",
                DestinationEventService.ANALYTICS,
                new StreamConfig.ProducerConfig(
                        new StreamConfig.MetricsPlatformClientConfig(
                            null,
                            Arrays.asList(
                                "page_id",
                                "page_namespace_id",
                                "page_namespace_text",
                                "page_title",
                                "page_is_redirect",
                                "page_revision_id",
                                "page_content_language",
                                "page_wikidata_id",
                                "page_user_groups_allowed_to_edit",
                                "page_user_groups_allowed_to_move",
                                "user_id",
                                "user_is_logged_in",
                                "user_name",
                                "user_groups",
                                "user_edit_count",
                                "user_edit_count_bucket",
                                "user_registration_timestamp",
                                "user_language",
                                "user_language_variant",
                                "user_is_bot",
                                "user_can_probably_edit_page",
                                "device_pixel_ratio",
                                "device_hardware_concurrency",
                                "device_max_touch_points",
                                "access_method",
                                "platform",
                                "platform_family",
                                "is_production"
                            ), CurationFilter.builder()
                                .pageTitleRules(CurationRules.<String>builder().isEquals("Test").build())
                                .userGroupsRules(
                                        CollectionCurationRules.<String>builder()
                                                .doesNotContain("sysop")
                                                .containsAny(Arrays.asList("steward", "bureaucrat"))
                                                .build()
                                )
                                .build()
                        )
                )
        ));
    }};

    private final boolean shouldFail;

    public TestMetricsClientIntegration() {
        this(false);
    }

    public TestMetricsClientIntegration(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    // Page

    @Override
    public Integer getPageId() {
        return 1;
    }

    @Override
    public Integer getPageNamespaceId() {
        return 0;
    }

    @Override
    public String getPageNamespaceText() {
        return "";
    }

    @Override
    public String getPageTitle() {
        return "Test";
    }

    @Override
    public Boolean getPageIsRedirect() {
        return false;
    }

    @Override
    public Integer getPageRevisionId() {
        return 1;
    }

    @Override
    public String getPageWikidataItemId() {
        return "Q1";
    }

    @Override
    public String getPageContentLanguage() {
        return "zh";
    }

    @Override
    public Collection<String> getPageGroupsAllowedToEdit() {
        return Collections.emptySet();
    }

    @Override
    public Collection<String> getPageGroupsAllowedToMove() {
        return Collections.emptySet();
    }

    // User

    @Override
    public Integer getUserId() {
        return 1;
    }

    @Override
    public Boolean getUserIsLoggedIn() {
        return true;
    }

    @Override
    public Boolean getUserIsBot() {
        return false;
    }

    @Override
    public String getUserName() {
        return "TestUser";
    }

    @Override
    public List<String> getUserGroups() {
        return singletonList("*");
    }

    @Override
    public Boolean getUserCanProbablyEditPage() {
        return true;
    }

    @Override
    public Integer getUserEditCount() {
        return 10;
    }

    @Override
    public String getUserEditCountBucket() {
        return "5-99 edits";
    }

    @Override
    public Long getUserRegistrationTimestamp() {
        return 1427224089000L;
    }

    @Override
    public String getUserLanguage() {
        return "zh";
    }

    @Override
    public String getUserLanguageVariant() {
        return "zh-tw";
    }

    // Device

    @Override
    public Float getDevicePixelRatio() {
        return 1.0f;
    }

    @Override
    public Integer getDeviceHardwareConcurrency() {
        return 1;
    }

    @Override
    public Integer getDeviceMaxTouchPoints() {
        return 1;
    }

    @Override
    public Boolean isProduction() {
        return true;
    }

    @Override
    public String getAppInstallId() {
        return "6f31a4fa-0a77-4c65-9994-f242fa58ce94";
    }

    public Map<String, StreamConfig> fetchStreamConfigs() throws IOException {
        if (shouldFail) {
            throw new IOException();
        }

        return STREAM_CONFIGS;
    }

    @Override
    public void sendEvents(String baseUri, Collection<Event> events) throws IOException {
        if (shouldFail) {
            throw new IOException();
        }
    }

}
