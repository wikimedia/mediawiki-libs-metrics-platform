package org.wikimedia.metrics_platform.event;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;

import org.wikimedia.metrics_platform.context.ClientData;
import org.wikimedia.metrics_platform.context.CustomData;
import org.wikimedia.metrics_platform.utils.Objects;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
@ParametersAreNullableByDefault
public class Event {
    @SerializedName("$schema") protected String schema;
    @SerializedName("name") protected final String name;
    @SerializedName("dt") protected String timestamp;
    @SerializedName("custom_data") protected Map<String, CustomData> customData;
    protected final Meta meta;
    @SerializedName("client_data") protected ClientData clientData;

    public Event(String schema, String stream, String name) {
        this.schema = schema;
        this.meta = new Meta(stream);
        this.name = name;
    }

    @Nullable
    public String getStream() {
        return meta.getStream();
    }

    public void setDomain(String domain) {
        meta.domain = domain;
    }

    @Nonnull
    public ClientData getClientData() {
        clientData = Objects.firstNonNull(clientData, ClientData::new);
        return clientData;
    }

    public void setCustomData(@Nonnull Map<String, CustomData> customData) {
        this.customData = customData;
    }

    @Data
    protected static final class Meta {
        private final String stream;
        private String domain;
    }
}
