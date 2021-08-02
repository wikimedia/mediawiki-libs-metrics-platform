package org.wikimedia.metrics_platform;

import java.util.List;
import java.util.Map;

public interface MetricsClientIntegration {

    String getAppInstallId();

    void fetchStreamConfigs(FetchStreamConfigsCallback callback);

    void sendEvents(String baseUri, List<Event> events, SendEventsCallback callback);

    interface FetchStreamConfigsCallback {
        void onSuccess(Map<String, StreamConfig> streamConfigs);
        void onFailure();
    }

    interface SendEventsCallback {
        void onSuccess();
        void onFailure();
    }

}
