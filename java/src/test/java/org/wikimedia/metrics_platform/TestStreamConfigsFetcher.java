package org.wikimedia.metrics_platform;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.wikimedia.metrics_platform.curation.CurationFilter;
import org.wikimedia.metrics_platform.curation.rules.CollectionCurationRules;
import org.wikimedia.metrics_platform.curation.rules.CurationRules;

public class TestStreamConfigsFetcher implements StreamConfigsFetcher {
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

    TestStreamConfigsFetcher() {
        this(false);
    }

    public TestStreamConfigsFetcher(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public Map<String, StreamConfig> fetchStreamConfigs() throws IOException {
        if (shouldFail) {
            throw new IOException();
        }

        return STREAM_CONFIGS;
    }

}
