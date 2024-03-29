package org.wikimedia.metrics_platform.context;

import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Page context data context fields.
 *
 * PageData are dynamic context fields that change with every request. PageData is submitted with each event
 * by the client and then queued for processing by EventProcessor.
 *
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
@Data @Builder @AllArgsConstructor
@ParametersAreNullableByDefault
public class PageData {

    public static final PageData NULL_PAGE_DATA = PageData.builder().build();

    private final Integer id;
    private final String title;
    @SerializedName("namespace_id") private final Integer namespaceId;
    @SerializedName("namespace_name") private final String namespaceName;
    @SerializedName("revision_id") private final Long revisionId;
    @SerializedName("wikidata_qid") private final String wikidataItemQid;
    @SerializedName("content_language") private final String contentLanguage;
}
