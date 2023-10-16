package org.wikimedia.metrics_platform.config.curation;

import java.util.Collection;

import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Value;

@Builder @Value
@ParametersAreNullableByDefault
public class ComparableCurationRules<T extends Comparable<T>> {
    @SerializedName("is_equals") T isEquals;
    @SerializedName("not_equals") T isNotEquals;
    @SerializedName("greater_than") T greaterThan;
    @SerializedName("less_than") T lessThan;
    @SerializedName("greater_than_or_equals") T greaterThanOrEquals;
    @SerializedName("less_than_or_equals") T lessThanOrEquals;
    Collection<T> in;
    @SerializedName("not_in") Collection<T> notIn;
}
