package org.wikimedia.metrics_platform.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import javax.annotation.concurrent.ThreadSafe;

import com.google.gson.annotations.SerializedName;

import lombok.NonNull;
import lombok.Value;

@Value @ThreadSafe
@ParametersAreNullableByDefault
public class StreamConfig {

    @SerializedName("stream") String streamName;

    @SerializedName("schema_title") String schemaTitle;

    @SerializedName("destination_event_service")
    DestinationEventService destinationEventService;

    @SerializedName("producers") ProducerConfig producerConfig;

    @SerializedName("sample") SampleConfig sampleConfig;

    /**
     * The context attributes that the Metrics Platform Client can add to an event.
     */
    public static final String[] CONTEXTUAL_ATTRIBUTES = new String[] {
        // Agent
        "agent_app_install_id",
        "agent_client_platform",
        "agent_client_platform_family",
        // Page
        "page_id",
        "page_title",
        "page_namespace",
        "page_namespace_name",
        "page_revision_id",
        "page_wikidata_qid",
        "page_content_language",
        "page_is_redirect",
        "page_user_groups_allowed_to_move",
        "page_user_groups_allowed_to_edit",
        // MediaWiki
        "mediawiki_skin",
        "mediawiki_version",
        "mediawiki_is_production",
        "mediawiki_is_debug_mode",
        "mediawiki_database",
        "mediawiki_site_content_language",
        "mediawiki_site_content_language_variant",
        // Performer
        "performer_is_logged_in",
        "performer_id",
        "performer_name",
        "performer_session_id",
        "performer_pageview_id",
        "performer_groups",
        "performer_is_bot",
        "performer_language",
        "performer_language_variant",
        "performer_can_probably_edit_page",
        "performer_edit_count",
        "performer_edit_count_bucket",
        "performer_registration_dt",
    };

    public boolean hasRequestedContextValuesConfig() {
        return producerConfig != null &&
                producerConfig.metricsPlatformClientConfig != null &&
                producerConfig.metricsPlatformClientConfig.requestedValues != null;
    }

    public boolean hasSampleConfig() {
        return producerConfig != null &&
                producerConfig.metricsPlatformClientConfig != null &&
                sampleConfig != null;
    }

    /**
     * Return whether this stream has any events it is interested in.
     *
     * @return true if the stream has events
     */
    public boolean hasEvents() {
        return producerConfig != null &&
            producerConfig.metricsPlatformClientConfig != null &&
            producerConfig.metricsPlatformClientConfig.events != null;
    }

    /**
     * Return the event names this stream is interested in.
     *
     * @return event names for the stream
     */
    public Set<String> getEvents() {
        if (hasEvents()) {
            return producerConfig.metricsPlatformClientConfig.events;
        }
        return Collections.emptySet();
    }

    public DestinationEventService getDestinationEventService() {
        return destinationEventService != null ? destinationEventService : DestinationEventService.ANALYTICS;
    }

    public boolean hasCurationFilter() {
        return producerConfig != null &&
            producerConfig.metricsPlatformClientConfig != null &&
            producerConfig.metricsPlatformClientConfig.curationFilter != null;
    }

    @NonNull
    public CurationFilter getCurationFilter() {
        if (hasCurationFilter()) {
            return producerConfig.metricsPlatformClientConfig.curationFilter;
        }

        return CurationFilter.builder().build();
    }

    @Value @ThreadSafe
    public static class ProducerConfig {
        @SerializedName("metrics_platform_client")
        StreamConfig.MetricsPlatformClientConfig metricsPlatformClientConfig;
    }

    @Value @ThreadSafe
    public static class MetricsPlatformClientConfig {
        @SerializedName("events") Set<String> events;
        @SerializedName("provide_values") Collection<String> requestedValues;
        @SerializedName("curation") CurationFilter curationFilter;
    }

    public boolean isInterestedInEvent(@Nonnull String eventName) {
        for (String streamEventName : getEvents()) {
            // Match string prefixes for event names of interested streams.
            if (eventName.startsWith(streamEventName)) {
                return true;
            }
        }
        return false;
    }
}
