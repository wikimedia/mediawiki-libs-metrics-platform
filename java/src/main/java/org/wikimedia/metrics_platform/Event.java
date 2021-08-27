package org.wikimedia.metrics_platform;

import com.google.gson.annotations.SerializedName;

public class Event {

    @SerializedName("$schema") private final String schema;
    private final Meta meta;
    private String dt;
    @SerializedName("app_install_id") private String appInstallId;
    @SerializedName("app_session_id") private String appSessionId;

    public Event(String schema, String stream) {
        this.schema = schema;
        this.meta = new Meta(stream);
    }

    String getStream() {
        return meta.getStream();
    }

    String getSchema() {
        return schema;
    }

    String getAppInstallId() {
        return appInstallId;
    }

    void setAppInstallId(String appInstallId) {
        this.appInstallId = appInstallId;
    }


    String getAppSessionId() {
        return appSessionId;
    }

    void setAppSessionId(String sessionId) {
        this.appSessionId = sessionId;
    }

    String getTimestamp() {
        return dt;
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
