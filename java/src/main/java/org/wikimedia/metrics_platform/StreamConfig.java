package org.wikimedia.metrics_platform;

import java.util.Collection;
import java.util.Set;

import javax.annotation.ParametersAreNullableByDefault;
import javax.annotation.concurrent.ThreadSafe;

import org.wikimedia.metrics_platform.curation.CurationFilter;

import com.google.gson.annotations.SerializedName;

import lombok.Value;

@Value @ThreadSafe
@ParametersAreNullableByDefault
public class StreamConfig {

    @SerializedName("stream") private String streamName;

    @SerializedName("schema_title") private String schemaTitle;

    @SerializedName("destination_event_service") private DestinationEventService destinationEventService;

    @SerializedName("producers") private ProducerConfig producerConfig;

    public boolean hasRequestedContextValuesConfig() {
        return producerConfig != null &&
                producerConfig.metricsPlatformClientConfig != null &&
                producerConfig.metricsPlatformClientConfig.requestedValues != null;
    }

    public boolean hasSamplingConfig() {
        return producerConfig != null &&
                producerConfig.metricsPlatformClientConfig != null &&
                producerConfig.metricsPlatformClientConfig.samplingConfig != null;
    }

    /**
     * Return whether this stream has any events it is interested in.
     *
     * @return if the stream has events
     */
    public boolean hasEvents() {
        return producerConfig != null &&
            producerConfig.metricsPlatformClientConfig != null &&
            producerConfig.metricsPlatformClientConfig.events != null;
    }

    /**
     * Return the event objects this stream is interested in.
     *
     * @return event objects for the stream
     */
    public Set<String> getEvents() {
        if (hasEvents()) {
            return producerConfig.metricsPlatformClientConfig.events;
        }
        return null;
    }

    DestinationEventService getDestinationEventService() {
        return destinationEventService != null ? destinationEventService : DestinationEventService.ANALYTICS;
    }

    @Value @ThreadSafe
    public static class ProducerConfig {
        @SerializedName("metrics_platform_client")
        StreamConfig.MetricsPlatformClientConfig metricsPlatformClientConfig;
    }

    @Value @ThreadSafe
    public static class MetricsPlatformClientConfig {
        @SerializedName("sampling") SamplingConfig samplingConfig;
        @SerializedName("provide_values") Collection<String> requestedValues;
        @SerializedName("curation") CurationFilter curationFilter;
        @SerializedName("events") Set<String> events;
    }
}
