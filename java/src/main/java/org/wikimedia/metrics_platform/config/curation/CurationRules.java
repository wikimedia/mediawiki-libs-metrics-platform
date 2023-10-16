package org.wikimedia.metrics_platform.config.curation;

import java.util.Collection;

import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Value;

@Builder @Value
@ParametersAreNullableByDefault
public class CurationRules<T> {
    @SerializedName("equals") T isEquals;
    @SerializedName("not_equals") T isNotEquals;
    Collection<T> in;
    @SerializedName("not_in") Collection<T> notIn;
}
