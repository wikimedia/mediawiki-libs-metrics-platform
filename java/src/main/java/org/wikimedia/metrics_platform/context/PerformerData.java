package org.wikimedia.metrics_platform.context;

import java.util.Collection;

import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Performer context data fields.
 *
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ParametersAreNullableByDefault
public class PerformerData {
    private Integer id;
    @SerializedName("name") private String name;
    @SerializedName("is_logged_in") private Boolean isLoggedIn;
    @SerializedName("session_id") private String sessionId;
    @SerializedName("pageview_id") private String pageviewId;
    @SerializedName("groups") private Collection<String> groups;
    @SerializedName("is_bot") private Boolean isBot;
    @SerializedName("language") private String language;
    @SerializedName("language_variant") private String languageVariant;
    @SerializedName("can_probably_edit_page") private Boolean canProbablyEditPage;
    @SerializedName("edit_count") private Integer editCount;
    @SerializedName("edit_count_bucket") private String editCountBucket;
    @SerializedName("registration_dt") private Long registrationDt;
}
