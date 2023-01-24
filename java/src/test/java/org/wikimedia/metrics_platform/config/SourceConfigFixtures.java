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
            "agent_app_install_id",
            "agent_client_platform",
            "agent_client_platform_family",
            "mediawiki_skin",
            "mediawiki_version",
            "mediawiki_is_production",
            "mediawiki_is_debug_mode",
            "mediawiki_database",
            "mediawiki_site_content_language",
            "mediawiki_site_content_language_variant",
            "page_id",
            "page_namespace",
            "page_namespace_name",
            "page_title",
            "page_is_redirect",
            "page_revision_id",
            "page_content_language",
            "page_wikidata_id",
            "page_user_groups_allowed_to_edit",
            "page_user_groups_allowed_to_move",
            "performer_id",
            "performer_is_logged_in",
            "performer_name",
            "performer_groups",
            "performer_edit_count",
            "performer_edit_count_bucket",
            "performer_registration_dt",
            "performer_language",
            "performer_language_variant",
            "performer_is_bot",
            "performer_can_probably_edit_page",
            "device_pixel_ratio",
            "device_hardware_concurrency",
            "device_max_touch_points",
            "access_method"
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
                        .performerGroupsRules(
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
