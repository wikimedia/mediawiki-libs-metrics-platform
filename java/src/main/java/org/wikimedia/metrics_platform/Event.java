package org.wikimedia.metrics_platform;

import java.util.Set;

import javax.annotation.ParametersAreNullableByDefault;

import org.wikimedia.metrics_platform.context.AgentData;
import org.wikimedia.metrics_platform.context.CustomData;
import org.wikimedia.metrics_platform.context.DeviceData;
import org.wikimedia.metrics_platform.context.MediawikiData;
import org.wikimedia.metrics_platform.context.PageData;
import org.wikimedia.metrics_platform.context.PerformerData;

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
    @SerializedName("agent") @NonNull private AgentData agentData = new AgentData();
    @SerializedName("mediawiki") @NonNull private MediawikiData mediawikiData = new MediawikiData();
    @SerializedName("page") @NonNull private PageData pageData = new PageData();
    @SerializedName("performer") @NonNull private PerformerData performerData = new PerformerData();
    @SerializedName("device") @NonNull private DeviceData deviceData = new DeviceData();
    @SerializedName("custom_data") private Set<CustomData> customData;

    @SerializedName("access_method") private String accessMethod;

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
