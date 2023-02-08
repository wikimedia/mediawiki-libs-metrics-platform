package org.wikimedia.metrics_platform.curation;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder @EqualsAndHashCode @ToString
@ParametersAreNullableByDefault
public class CurationRules<T> {
    @SerializedName("equals") private T isEquals;
    @SerializedName("not_equals") private T notEquals;
    private Collection<T> in;
    @SerializedName("not_in") private Collection<T> notIn;

    public boolean apply(@Nonnull T value) {
        if (isEquals != null && !isEquals.equals(value)) {
            return false;
        }
        if (notEquals != null && notEquals.equals(value)) {
            return false;
        }
        if (in != null && !in.contains(value)) {
            return false;
        }
        if (notIn != null && notIn.contains(value)) {
            return false;
        }
        return true;
    }

}
