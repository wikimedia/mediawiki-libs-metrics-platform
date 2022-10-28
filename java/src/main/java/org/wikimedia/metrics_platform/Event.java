package org.wikimedia.metrics_platform;

import java.util.Set;

import javax.annotation.ParametersAreNullableByDefault;

import org.wikimedia.metrics_platform.context.CustomData;
import org.wikimedia.metrics_platform.context.DeviceData;
import org.wikimedia.metrics_platform.context.PageData;
import org.wikimedia.metrics_platform.context.UserData;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NonNull;

@Data
@ParametersAreNullableByDefault
public class Event {

    @SerializedName("$schema") private String schema;
    private final Meta meta;
    @SerializedName("name") private final String name;
    @SerializedName("dt") private String timestamp;
    @SerializedName("app_install_id") private String appInstallId;
    @SerializedName("app_session_id") private String appSessionId;

    @SerializedName("page") @NonNull private PageData pageData = new PageData();
    @SerializedName("user") @NonNull private UserData userData = new UserData();
    @SerializedName("device") @NonNull private DeviceData deviceData = new DeviceData();
    @SerializedName("custom_data") private Set<CustomData> customData;

    @SerializedName("access_method") private String accessMethod;
    private String platform;
    @SerializedName("platform_family") private String platformFamily;
    @SerializedName("is_production") private Boolean isProduction;

    public Event(String schema, String stream, String name) {
        this.schema = schema;
        this.meta = new Meta(stream);
        this.name = name;
    }

    public String getStream() {
        return meta.getStream();
    }

    @Data
    private static final class Meta {
        private final String stream;
    }

}
