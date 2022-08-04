package org.wikimedia.metrics_platform.context;

import java.util.Collection;

import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User context data context fields.
 *
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@ParametersAreNullableByDefault
public class UserData {
    private Integer id;
    private String name;
    private Collection<String> groups;
    @SerializedName("is_logged_in") private Boolean isLoggedIn;
    @SerializedName("is_bot") private Boolean isBot;
    @SerializedName("can_probably_edit_page") private Boolean canProbablyEditPage;
    @SerializedName("edit_count") private Integer editCount;
    @SerializedName("edit_count_bucket") private String editCountBucket;
    @SerializedName("registration_timestamp") private Long registrationTimestamp;
    private String language;
    @SerializedName("language_variant") private String languageVariant;
}
