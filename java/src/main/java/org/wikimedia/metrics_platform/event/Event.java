package org.wikimedia.metrics_platform.event;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.ParametersAreNullableByDefault;

import org.wikimedia.metrics_platform.context.CustomData;
import org.wikimedia.metrics_platform.context.PageData;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
@ParametersAreNullableByDefault
public class Event {
    @SerializedName("$schema") protected String schema;
    @SerializedName("name") protected final String name;
    @SerializedName("dt") protected String timestamp;
    @SerializedName("custom_data") protected Map<String, Object> customData;
    protected final Meta meta;
    @SerializedName("page") protected PageData pageData;

    public Event(String schema, String stream, String name) {
        this.schema = schema;
        this.meta = new Meta(stream);
        this.name = name;
    }

    public String getStream() {
        return meta.getStream();
    }

    public void setDomain(String domain) {
        this.meta.domain = domain;
    }

    public PageData getPageData() {
        if (this.pageData == null) this.pageData = new PageData();
        return this.pageData;
    }

    public void setCustomData(Set<CustomData>customDataSet) {
        Map<String, Object> formattedCustomData = new HashMap<>();
        for (CustomData customDataEach : customDataSet) {
            Map<String, String> data = new HashMap<>();
            data.put("data_type", customDataEach.getType().toString().toLowerCase(Locale.ROOT));
            data.put("value", customDataEach.getValue());
            formattedCustomData.put(customDataEach.getName(), data);
        }
        this.customData = formattedCustomData;
    }

    public void setCustomData(Map<String, Object> customData) {
        this.customData = customData;
    }

    @Data
    protected static final class Meta {
        private final String stream;
        private String domain;
    }
}
