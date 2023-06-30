package org.wikimedia.metrics_platform.context;

import java.time.Instant;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Performer context data fields.
 *
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
@Data
@Builder
@AllArgsConstructor
@ParametersAreNullableByDefault
public class PerformerData {

    public static final PerformerData NULL_PERFORMER_DATA = PerformerData.builder().build();

    @SerializedName("name") private final String name;
    @SerializedName("is_logged_in") private final Boolean isLoggedIn;
    private final Integer id;
    @SerializedName("session_id") private final String sessionId;
    @SerializedName("pageview_id") private final String pageviewId;
    @SerializedName("groups") private final Collection<String> groups;
    @SerializedName("is_bot") private final Boolean isBot;
    @SerializedName("language") private final String language;
    @SerializedName("language_variant") private final String languageVariant;
    @SerializedName("can_probably_edit_page") private final Boolean canProbablyEditPage;
    @SerializedName("edit_count") private final Integer editCount;
    @SerializedName("edit_count_bucket") private final String editCountBucket;
    @SerializedName("registration_dt") private final Instant registrationDt;

    public static PerformerDataBuilder builderFrom(@Nonnull PerformerData performerData) {
        return builder()
                .name(performerData.getName())
                .isLoggedIn(performerData.getIsLoggedIn())
                .id(performerData.getId())
                .sessionId(performerData.getSessionId())
                .pageviewId(performerData.getPageviewId())
                .groups(performerData.getGroups())
                .isBot(performerData.getIsBot())
                .language(performerData.getLanguage())
                .languageVariant(performerData.getLanguageVariant())
                .canProbablyEditPage(performerData.getCanProbablyEditPage())
                .editCount(performerData.getEditCount())
                .editCountBucket(performerData.getEditCountBucket())
                .registrationDt(performerData.getRegistrationDt());
    }
}
