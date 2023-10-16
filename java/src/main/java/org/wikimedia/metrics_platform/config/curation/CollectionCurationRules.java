package org.wikimedia.metrics_platform.config.curation;

import java.util.Collection;

import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Value;

@Builder @Value
@ParametersAreNullableByDefault
public class CollectionCurationRules<T> {
    T contains;
    @SerializedName("does_not_contain") T doesNotContain;
    @SerializedName("contains_all") Collection<T> containsAll;
    @SerializedName("contains_any") Collection<T> containsAny;
}
