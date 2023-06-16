package org.wikimedia.metrics_platform.event;

import static org.wikimedia.metrics_platform.utils.Objects.firstNonNull;

import java.util.Map;

import javax.annotation.Nonnull;

import org.wikimedia.metrics_platform.context.AgentData;
import org.wikimedia.metrics_platform.context.ClientData;
import org.wikimedia.metrics_platform.context.CustomData;
import org.wikimedia.metrics_platform.context.MediawikiData;
import org.wikimedia.metrics_platform.context.PageData;
import org.wikimedia.metrics_platform.context.PerformerData;

import com.google.gson.annotations.SerializedName;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class EventProcessed extends Event {
    @SerializedName("agent") private AgentData agentData;
    @SerializedName("page") private PageData pageData;
    @SerializedName("mediawiki") private MediawikiData mediawikiData;
    @SerializedName("performer") private PerformerData performerData;

    public EventProcessed(
            String schema,
            String stream,
            String name,
            Map<String, CustomData> customData,
            ClientData clientData) {
        super(schema, stream, name);
        this.agentData = clientData.getAgentData();
        this.pageData = clientData.getPageData();
        this.mediawikiData = clientData.getMediawikiData();
        this.performerData = clientData.getPerformerData();
        this.setCustomData(customData);
    }

    @Nonnull
    public static EventProcessed fromEvent(Event event) {
        return new EventProcessed(
                event.getSchema(),
                event.getStream(),
                event.getName(),
                event.getCustomData(),
                event.getClientData()
        );
    }

    @Nonnull
    public AgentData getAgentData() {
        agentData = firstNonNull(agentData, AgentData::new);
        return agentData;
    }

    @Nonnull
    public PageData getPageData() {
        pageData = firstNonNull(pageData, PageData::new);
        return pageData;
    }

    @Nonnull
    public MediawikiData getMediawikiData() {
        mediawikiData = firstNonNull(mediawikiData, MediawikiData::new);
        return mediawikiData;
    }

    @Nonnull
    public PerformerData getPerformerData() {
        performerData = firstNonNull(performerData, PerformerData::new);
        return performerData;
    }

    @Override
    public void setClientData(@Nonnull ClientData clientData) {
        setAgentData(clientData.getAgentData());
        setPageData(clientData.getPageData());
        setMediawikiData(clientData.getMediawikiData());
        setPerformerData(clientData.getPerformerData());
    }

    public void setAgentData(AgentData agentData) {
        this.agentData = agentData;
    }
    public void setPageData(PageData pageData) {
        this.pageData = pageData;
    }
    public void setMediawikiData(MediawikiData mediawikiData) {
        this.mediawikiData = mediawikiData;
    }
    public void setPerformerData(PerformerData performerData) {
        this.performerData = performerData;
    }

}
