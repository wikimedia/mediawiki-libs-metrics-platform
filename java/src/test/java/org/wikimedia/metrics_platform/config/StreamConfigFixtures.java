package org.wikimedia.metrics_platform.config;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.wikimedia.metrics_platform.config.StreamConfigFetcher.METRICS_PLATFORM_SCHEMA_TITLE;
import static org.wikimedia.metrics_platform.context.ContextValue.AGENT_CLIENT_PLATFORM;
import static org.wikimedia.metrics_platform.context.ContextValue.AGENT_CLIENT_PLATFORM_FAMILY;
import static org.wikimedia.metrics_platform.context.ContextValue.MEDIAWIKI_DATABASE;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_TITLE;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_SESSION_ID;
import static org.wikimedia.metrics_platform.curation.CurationFilterFixtures.curationFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class StreamConfigFixtures {

    public static final Map<String, StreamConfig> STREAM_CONFIGS_WITH_EVENTS = new HashMap<String, StreamConfig>() {{
            put("test.stream", sampleStreamConfig(true));
        }
    };

    private StreamConfigFixtures() {
        // Utility class, should never be instantiated
    }

    public static StreamConfig sampleStreamConfig(boolean hasEvents) {
        Set<String> emptyEvents = emptySet();
        Set<String> testEvents = new HashSet<>(singletonList("test.event"));
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
            "page_wikidata_qid",
            "page_user_groups_allowed_to_edit",
            "page_user_groups_allowed_to_move",
            "performer_id",
            "performer_is_logged_in",
            "performer_name",
            "performer_session_id",
            "performer_pageview_id",
            "performer_groups",
            "performer_edit_count",
            "performer_edit_count_bucket",
            "performer_registration_dt",
            "performer_language",
            "performer_language_variant",
            "performer_is_bot",
            "performer_can_probably_edit_page"
        ));
        SampleConfig sampleConfig = new SampleConfig(1.0, SampleConfig.Identifier.PAGEVIEW);

        return new StreamConfig(
            "test.stream",
            "test/event",
            DestinationEventService.ANALYTICS,
            new StreamConfig.ProducerConfig(
                new StreamConfig.MetricsPlatformClientConfig(
                    events,
                    requestedValuesSet,
                        CurationFilterFixtures.getCurationFilter()
                )
            ),
            sampleConfig
        );
    }

    /**
     * Convenience method for getting stream config.
     */
    public static StreamConfig streamConfig(CurationFilter curationFilter) {
        String[] provideValues = {
            AGENT_CLIENT_PLATFORM,
            AGENT_CLIENT_PLATFORM_FAMILY,
            PAGE_TITLE,
            MEDIAWIKI_DATABASE,
            PERFORMER_SESSION_ID
        };
        Set<String> events = Collections.singleton("test_event");
        SampleConfig sampleConfig = new SampleConfig(1.0f, SampleConfig.Identifier.PAGEVIEW);

        return new StreamConfig(
            "test_stream",
            METRICS_PLATFORM_SCHEMA_TITLE,
            DestinationEventService.LOCAL,
            new StreamConfig.ProducerConfig(
                new StreamConfig.MetricsPlatformClientConfig(
                    events,
                    Arrays.asList(provideValues),
                    curationFilter
                )
            ),
            sampleConfig
        );
    }

    /**
     * Convenience method for getting a stream config map.
     */
    public static Map<String, StreamConfig> streamConfigMap() {
        return streamConfigMap(curationFilter());
    }

    public static Map<String, StreamConfig> streamConfigMap(CurationFilter curationFilter) {
        StreamConfig streamConfig = streamConfig(curationFilter);
        return singletonMap(streamConfig.getStreamName(), streamConfig);
    }
}
