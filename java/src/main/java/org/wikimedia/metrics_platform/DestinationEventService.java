package org.wikimedia.metrics_platform;

import com.google.gson.annotations.SerializedName;

/**
 * Possible event destination endpoints which can be specified in stream configurations.
 * For now, we'll assume that we always want to send to ANALYTICS.
 *
 * https://wikitech.wikimedia.org/wiki/Event_Platform/EventGate#EventGate_clusters
 */
enum DestinationEventService {

    @SerializedName("eventgate-analytics-external") ANALYTICS (
            "https://intake-analytics.wikimedia.org"
            ),

    @SerializedName("eventgate-logging-external") ERROR_LOGGING (
            "https://intake-logging.wikimedia.org"
            );

    private final String baseUri;

    DestinationEventService(String baseUri) {
        this.baseUri = baseUri;
    }

    public String getBaseUri() {
        return baseUri;
    }

}
