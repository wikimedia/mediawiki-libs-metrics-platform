package org.wikimedia.metrics_platform.event;

import java.util.Map;

import org.wikimedia.metrics_platform.context.AgentData;
import org.wikimedia.metrics_platform.context.ClientData;
import org.wikimedia.metrics_platform.context.CustomData;
import org.wikimedia.metrics_platform.context.PageData;
import org.wikimedia.metrics_platform.context.MediawikiData;
import org.wikimedia.metrics_platform.context.PerformerData;

import com.google.gson.annotations.SerializedName;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class EventProcessed extends Event {
    @SerializedName("agent") private AgentData agentData;
    @SerializedName("mediawiki") private MediawikiData mediawikiData;
    @SerializedName("performer") private PerformerData performerData;

    public EventProcessed(
            String schema,
            String stream,
            String name,
            Map<String, CustomData> customData,
            PageData pageData) {
        super(schema, stream, name);
        this.agentData = new AgentData();
        this.mediawikiData = new MediawikiData();
        this.performerData = new PerformerData();
        this.setCustomData(customData);
        this.setPageData(pageData);
    }

    public static EventProcessed fromEvent(Event event) {
        return new EventProcessed(
                event.getSchema(),
                event.getStream(),
                event.getName(),
                event.getCustomData(),
                event.getPageData()
        );
    }

    public AgentData getAgentData() {
        if (this.agentData == null) this.agentData = new AgentData();
        return this.agentData;
    }

    public MediawikiData getMediawikiData() {
        if (this.mediawikiData == null) this.mediawikiData = new MediawikiData();
        return this.mediawikiData;
    }

    public PerformerData getPerformerData() {
        if (this.performerData == null) this.performerData = new PerformerData();
        return this.performerData;
    }

    public void setClientData(ClientData clientData) {
        setAgentData(clientData.getAgentData());
        setMediawikiData(clientData.getMediawikiData());
        setPerformerData(clientData.getPerformerData());
    }

    public void setAgentData(AgentData agentData) {
        this.agentData = agentData;
    }
    public void setMediawikiData(MediawikiData mediawikiData) {
        this.mediawikiData = mediawikiData;
    }
    public void setPerformerData(PerformerData performerData) {
        this.performerData = performerData;
    }

}
