package org.wikimedia.metrics_platform;

import com.google.gson.annotations.SerializedName;

public class Event {

    @SerializedName("$schema") private final String schema;
    private final Meta meta;
    private String dt;
    @SerializedName("app_session_id") private String sessionId;
    @SerializedName("app_install_id") private String appInstallId;

    public Event(String schema, String stream) {
        this.schema = schema;
        this.meta = new Meta(stream);
    }

    String getStream() {
        return meta.getStream();
    }

    void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    void setAppInstallId(String appInstallId) {
        this.appInstallId = appInstallId;
    }

    void setTimestamp(String dt) {
        this.dt = dt;
    }

    private static final class Meta {
        private final String stream;

        private Meta(String stream) {
            this.stream = stream;
        }

        private String getStream() {
            return stream;
        }
    }

}
