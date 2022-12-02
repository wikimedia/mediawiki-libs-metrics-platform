package org.wikimedia.metrics_platform.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.wikimedia.metrics_platform.DestinationEventService;
import org.wikimedia.metrics_platform.curation.CurationFilter;
import org.wikimedia.metrics_platform.curation.rules.CollectionCurationRules;
import org.wikimedia.metrics_platform.curation.rules.CurationRules;

public final class SourceConfigFixtures {

    public static final Map<String, StreamConfig> STREAM_CONFIGS_WITH_EVENTS = new HashMap<String, StreamConfig>() {{
            put("test.stream", getSampleStreamConfig(true));
        }
    };

    private SourceConfigFixtures() {
        // Utility class, should never be instantiated
    }

    public static StreamConfig getSampleStreamConfig(boolean hasEvents) {
        Set<String> emptyEvents = Collections.<String>emptySet();
        Set<String> testEvents = new HashSet<>(Collections.singletonList("test.event"));
        Set<String> events = hasEvents ? testEvents : emptyEvents;
        Set<String> requestedValuesSet = new HashSet<>(Arrays.asList(
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
        ));
        SampleConfig sampleConfig = new SampleConfig(1.0, SampleConfig.Identifier.UNIT, "pageview");

        return new StreamConfig(
            "test.stream",
            "test/event",
            DestinationEventService.ANALYTICS,
            new StreamConfig.ProducerConfig(
                new StreamConfig.MetricsPlatformClientConfig(
                    events,
                    requestedValuesSet,
                    CurationFilter.builder()
                        .pageTitleRules(CurationRules.<String>builder().isEquals("Test").build())
                        .userGroupsRules(
                            CollectionCurationRules.<String>builder()
                                .doesNotContain("sysop")
                                .containsAny(Arrays.asList("steward", "bureaucrat"))
                                .build()
                        )
                        .build()
                )
            ),
            sampleConfig
        );
    }
}
