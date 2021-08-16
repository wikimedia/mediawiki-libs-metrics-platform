package org.wikimedia.metrics_platform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class TestMetricsClientIntegration implements MetricsClientIntegration {

    static final Map<String, StreamConfig> STREAM_CONFIGS = new HashMap<String, StreamConfig>(){{
        put("test.event", new StreamConfig(
                "test.event",
                "test/event",
                DestinationEventService.ANALYTICS,
                new StreamConfig.ProducerConfig(
                        new StreamConfig.ProducerConfig.MetricsPlatformClientConfig(
                                new SamplingConfig())
                )
        ));
    }};

    private final boolean shouldFail;

    TestMetricsClientIntegration() {
        this(false);
    }

    TestMetricsClientIntegration(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public String getAppInstallId() {
        return "6f31a4fa-0a77-4c65-9994-f242fa58ce94";
    }

    @Override
    public void fetchStreamConfigs(FetchStreamConfigsCallback callback) {
        callback.onSuccess(STREAM_CONFIGS);
    }

    @Override
    public void sendEvents(String baseUri, Collection<Event> events, SendEventsCallback callback) {
        if (shouldFail) {
            callback.onFailure();
        } else {
            callback.onSuccess();
        }
    }

}
