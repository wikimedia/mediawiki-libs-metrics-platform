package org.wikimedia.metrics_platform.context;

import static org.wikimedia.metrics_platform.utils.Objects.firstNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Client metadata context fields.
 *
 * ClientData includes immutable contextual data from the client as opposed to PageData which is dynamic
 * with each request. This metadata is added to every event submission when queued for processing.
 *
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ParametersAreNullableByDefault
public class ClientData {
    @SerializedName("agent") private AgentData agentData;
    @SerializedName("page") private PageData pageData;
    @SerializedName("mediawiki") private MediawikiData mediawikiData;
    @SerializedName("performer") private PerformerData performerData;
    @SerializedName("domain") private String domain;

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

    @Nullable
    public String getDomain() {
        return domain;
    }
}
