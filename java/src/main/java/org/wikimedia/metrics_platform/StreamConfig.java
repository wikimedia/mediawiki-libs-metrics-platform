package org.wikimedia.metrics_platform;

import java.util.Collection;

import javax.annotation.concurrent.ThreadSafe;

import org.wikimedia.metrics_platform.curation.CurationFilter;

import com.google.gson.annotations.SerializedName;

import lombok.Value;

@Value @ThreadSafe
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
    }
}
