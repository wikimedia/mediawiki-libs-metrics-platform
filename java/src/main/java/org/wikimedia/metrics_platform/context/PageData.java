package org.wikimedia.metrics_platform.context;

import java.util.Collection;

import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Page context data context fields.
 *
 * PageData are dynamic context fields that change with every request. PageData is submitted with each event
 * by the client and then queued for processing by EventProcessor.
 *
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@ParametersAreNullableByDefault
public class PageData {
    private Integer id;
    private String title;
    @SerializedName("namespace") private Integer namespace;
    @SerializedName("namespace_name") private String namespaceName;
    @SerializedName("revision_id") private Long revisionId;
    @SerializedName("wikidata_qid") private String wikidataItemQid;
    @SerializedName("content_language") private String contentLanguage;
    @SerializedName("is_redirect") private Boolean isRedirect;
    @SerializedName("user_groups_allowed_to_move") private Collection<String> groupsAllowedToMove;
    @SerializedName("user_groups_allowed_to_edit") private Collection<String> groupsAllowedToEdit;
}
