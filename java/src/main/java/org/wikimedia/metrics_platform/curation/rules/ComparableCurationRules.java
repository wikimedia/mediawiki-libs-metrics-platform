package org.wikimedia.metrics_platform.curation.rules;

import java.util.Collection;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder @EqualsAndHashCode @ToString
public class ComparableCurationRules<T extends Comparable<T>> {
    private T equals;
    @SerializedName("not_equals") private T notEquals;
    @SerializedName("greater_than") private T greaterThan;
    @SerializedName("less_than") private T lessThan;
    @SerializedName("greater_than_or_equals") private T greaterThanOrEquals;
    @SerializedName("less_than_or_equals") private T lessThanOrEquals;
    private Collection<T> in;
    @SerializedName("not_in") private Collection<T> notIn;

    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:NPathComplexity"})
    public boolean apply(T value) {
        if (equals != null && !equals.equals(value)) {
            return false;
        }
        if (notEquals != null && notEquals.equals(value)) {
            return false;
        }
        if (greaterThan != null && greaterThan.compareTo(value) >= 0) {
            return false;
        }
        if (lessThan != null && lessThan.compareTo(value) <= 0) {
            return false;
        }
        if (greaterThanOrEquals != null && greaterThanOrEquals.compareTo(value) > 0) {
            return false;
        }
        if (lessThanOrEquals != null && lessThanOrEquals.compareTo(value) < 0) {
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
