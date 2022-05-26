package org.wikimedia.metrics_platform.curation;

import org.wikimedia.metrics_platform.Event;
import org.wikimedia.metrics_platform.StreamConfig;

public class CurationController {

    public boolean eventPassesCurationRules(Event event, StreamConfig streamConfig) {
        StreamConfig.ProducerConfig producerConfig = streamConfig.getProducerConfig();
        if (producerConfig == null) {
            return true;
        }
        StreamConfig.ProducerConfig.MetricsPlatformClientConfig metricsPlatformClientConfig =
                producerConfig.getMetricsPlatformClientConfig();
        if (metricsPlatformClientConfig == null) {
            return true;
        }
        CurationFilter curationFilter = metricsPlatformClientConfig.getCurationFilter();
        if (curationFilter == null) {
            return true;
        }
        return curationFilter.apply(event);
    }

}
