package org.wikimedia.metrics_platform;

import static lombok.AccessLevel.PRIVATE;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Possible event destination endpoints which can be specified in stream configurations.
 * For now, we'll assume that we always want to send to ANALYTICS.
 *
 * https://wikitech.wikimedia.org/wiki/Event_Platform/EventGate#EventGate_clusters
 */
@Getter @RequiredArgsConstructor(access = PRIVATE)
public enum DestinationEventService {

    @SerializedName("eventgate-analytics-external")
    ANALYTICS("https://intake-analytics.wikimedia.org"),

    @SerializedName("eventgate-logging-external")
    ERROR_LOGGING("https://intake-logging.wikimedia.org"),

    @SerializedName("eventgate-logging-local")
    LOCAL("http://localhost:8192/v1/events");

    private final String baseUri;

}
