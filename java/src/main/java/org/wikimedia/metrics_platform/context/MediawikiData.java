package org.wikimedia.metrics_platform.context;

import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mediawiki context data fields.
 *
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ParametersAreNullableByDefault
public class MediawikiData {
    @SerializedName("skin") private String skin;
    @SerializedName("version") private String version;
    @SerializedName("is_production") private Boolean isProduction;
    @SerializedName("is_debug_mode") private Boolean isDebugMode;
    @SerializedName("database") private String database;
    @SerializedName("site_content_language") private String siteContentLanguage;
    @SerializedName("site_content_language_variant") private String siteContentLanguageVariant;
}
