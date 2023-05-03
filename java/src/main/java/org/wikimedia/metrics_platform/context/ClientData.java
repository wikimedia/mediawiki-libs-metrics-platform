package org.wikimedia.metrics_platform.context;

import java.util.function.Supplier;

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
    @SerializedName("mediawiki") private MediawikiData mediawikiData;
    @SerializedName("performer") private PerformerData performerData;
    @SerializedName("domain") private String domain;

    private <T> T firstNonNull(T first, Supplier<T> second) {
        if (first != null) return first;
        return second.get();
    }

    public AgentData getAgentData() {
        this.agentData = firstNonNull(agentData, AgentData::new);
        return this.agentData;
    }

    public MediawikiData getMediawikiData() {
        this.mediawikiData = firstNonNull(mediawikiData, MediawikiData::new);
        return this.mediawikiData;
    }

    public PerformerData getPerformerData() {
        this.performerData = firstNonNull(performerData, PerformerData::new);
        return this.performerData;
    }

    public String getDomain() {
        return this.domain;
    }
}
