package org.wikimedia.metrics_platform.event;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.ParametersAreNullableByDefault;

import org.wikimedia.metrics_platform.context.AgentData;
import org.wikimedia.metrics_platform.context.CustomData;
import org.wikimedia.metrics_platform.context.MediawikiData;
import org.wikimedia.metrics_platform.context.PageData;
import org.wikimedia.metrics_platform.context.PerformerData;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
@ParametersAreNullableByDefault
public class Event {
    @SerializedName("$schema") private String schema;
    @SerializedName("name") private final String name;
    @SerializedName("dt") private String timestamp;
    @SerializedName("custom_data") private Map<String, Object> customData;
    private final Meta meta;
    @SerializedName("agent") private AgentData agentData;
    @SerializedName("page") private PageData pageData;
    @SerializedName("mediawiki") private MediawikiData mediawikiData;
    @SerializedName("performer") private PerformerData performerData;

    public Event(String schema, String stream, String name) {
        this.schema = schema;
        this.meta = new Meta(stream);
        this.name = name;
    }

    public String getStream() {
        return meta.getStream();
    }

    public String getDomain() {
        return meta.getDomain();
    }

    public AgentData getAgentData() {
        if (this.agentData == null) {
            this.agentData = new AgentData();
        }
        return this.agentData;
    }

    public PageData getPageData() {
        if (this.pageData == null) {
            this.pageData = new PageData();
        }
        return this.pageData;
    }

    public MediawikiData getMediawikiData() {
        if (this.mediawikiData == null) {
            this.mediawikiData = new MediawikiData();
        }
        return this.mediawikiData;
    }

    public PerformerData getPerformerData() {
        if (this.performerData == null) {
            this.performerData = new PerformerData();
        }
        return this.performerData;
    }

    public void setDomain(String domain) {
        this.meta.domain = domain;
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

    @Data
    private static final class Meta {
        private final String stream;
        private String domain;
    }
}
