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
import java.util.List;
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
            "mediawiki_database",
            "page_id",
            "page_namespace_id",
            "page_namespace_name",
            "page_title",
            "page_revision_id",
            "page_content_language",
            "page_wikidata_qid",
            "performer_id",
            "performer_is_logged_in",
            "performer_is_temp",
            "performer_name",
            "performer_session_id",
            "performer_pageview_id",
            "performer_groups",
            "performer_registration_dt",
            "performer_language_groups",
            "performer_language_primary"
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
                    new HashSet<>(List.of(provideValues)),
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
