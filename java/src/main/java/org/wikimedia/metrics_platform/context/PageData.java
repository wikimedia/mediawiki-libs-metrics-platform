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
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@ParametersAreNullableByDefault
public class PageData {
    private Integer id;
    @SerializedName("namespace_id") private Integer namespaceId;
    @SerializedName("namespace_text") private String namespaceText;
    private String title;
    @SerializedName("is_redirect") private Boolean isRedirect;
    @SerializedName("revision_id") private Integer revisionId;
    @SerializedName("wikidata_id") private String wikidataItemId;
    @SerializedName("content_language") private String contentLanguage;
    @SerializedName("user_groups_allowed_to_edit") private Collection<String> groupsAllowedToEdit;
    @SerializedName("user_groups_allowed_to_move") private Collection<String> groupsAllowedToMove;
}
