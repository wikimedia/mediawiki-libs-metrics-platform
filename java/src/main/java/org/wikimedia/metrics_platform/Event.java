package org.wikimedia.metrics_platform;

import javax.annotation.ParametersAreNullableByDefault;

import org.wikimedia.metrics_platform.context.DeviceData;
import org.wikimedia.metrics_platform.context.PageData;
import org.wikimedia.metrics_platform.context.UserData;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NonNull;

@Data
@ParametersAreNullableByDefault
public class Event {

    @SerializedName("$schema") private final String schema;
    private final Meta meta;
    @SerializedName("dt") private String timestamp;
    @SerializedName("app_install_id") private String appInstallId;
    @SerializedName("app_session_id") private String appSessionId;

    @SerializedName("page") @NonNull private PageData pageData = new PageData();
    @SerializedName("user") @NonNull private UserData userData = new UserData();
    @SerializedName("device") @NonNull private DeviceData deviceData = new DeviceData();

    @SerializedName("access_method") private String accessMethod;
    private String platform;
    @SerializedName("platform_family") private String platformFamily;
    @SerializedName("is_production") private Boolean isProduction;

    public Event(String schema, String stream) {
        this.schema = schema;
        this.meta = new Meta(stream);
    }

    public String getStream() {
        return meta.getStream();
    }

    @Data
    private static final class Meta {
        private final String stream;
    }

}
