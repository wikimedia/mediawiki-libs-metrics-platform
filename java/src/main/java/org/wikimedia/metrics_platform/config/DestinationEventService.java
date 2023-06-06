package org.wikimedia.metrics_platform.config;

import java.net.URL;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.SneakyThrows;

/**
 * Possible event destination endpoints which can be specified in stream configurations.
 * For now, we'll assume that we always want to send to ANALYTICS.
 *
 * https://wikitech.wikimedia.org/wiki/Event_Platform/EventGate#EventGate_clusters
 */
@Getter
public enum DestinationEventService {

    @SerializedName("eventgate-analytics-external")
    ANALYTICS("https://intake-analytics.wikimedia.org"),

    @SerializedName("eventgate-logging-external")
    ERROR_LOGGING("https://intake-logging.wikimedia.org"),

    @SerializedName("eventgate-logging-local")
    LOCAL("http://localhost:8192");

    private final URL baseUri;

    @SneakyThrows
    DestinationEventService(String baseUri) {
        this.baseUri = new URL(baseUri + "/v1/events?hasty=true");
    }
}
