package org.wikimedia.metrics_platform.context;

import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Interaction data fields.
 *
 * Common interaction fields that describe the event being submitted. Most fields are nullable.
 */
@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ParametersAreNullableByDefault
public class InteractionData {

    public static final InteractionData NULL_INTERACTION_DATA = InteractionData.builder().build();

    @SerializedName("action") private final String action;
    @SerializedName("action_subtype") private final String actionSubtype;
    @SerializedName("action_source") private final String actionSource;
    @SerializedName("action_context") private final String actionContext;
    @SerializedName("element_id") private final String elementId;
    @SerializedName("element_friendly_name") private final String elementFriendlyName;
    @SerializedName("funnel_entry_token") private final String funnelEntryToken;
    @SerializedName("funnel_event_sequence_position") private final Integer funnelEventSequencePosition;
}
