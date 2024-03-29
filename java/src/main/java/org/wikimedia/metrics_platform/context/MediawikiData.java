package org.wikimedia.metrics_platform.context;

import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Mediawiki context data fields.
 *
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
@Builder
@Data
@AllArgsConstructor
@ParametersAreNullableByDefault
public class MediawikiData {

    public static final MediawikiData NULL_MEDIAWIKI_DATA = MediawikiData.builder().build();

    @SerializedName("database") private final String database;
}
