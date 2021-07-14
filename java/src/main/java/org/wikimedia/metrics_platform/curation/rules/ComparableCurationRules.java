package org.wikimedia.metrics_platform.curation.rules;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;

public class ComparableCurationRules<T extends Comparable<T>> {
    private T equals;
    @SerializedName("not_equals") private T notEquals;
    @SerializedName("greater_than") private T greaterThan;
    @SerializedName("less_than") private T lessThan;
    @SerializedName("greater_than_or_equals") private T greaterThanOrEquals;
    @SerializedName("less_than_or_equals") private T lessThanOrEquals;
    private Collection<T> in;
    @SerializedName("not_in") private Collection<T> notIn;

    public ComparableCurationRules(Builder<T> builder) {
        this.equals = builder.equals;
        this.notEquals = builder.notEquals;
        this.greaterThan = builder.greaterThan;
        this.lessThan = builder.lessThan;
        this.greaterThanOrEquals = builder.greaterThanOrEquals;
        this.lessThanOrEquals = builder.lessThanOrEquals;
        this.in = builder.in;
        this.notIn = builder.notIn;
    }

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

    public static class Builder<T extends Comparable<T>> {
        private T equals;
        private T notEquals;
        private T greaterThan;
        private T lessThan;
        private T greaterThanOrEquals;
        private T lessThanOrEquals;
        private Collection<T> in;
        private Collection<T> notIn;

        public ComparableCurationRules.Builder<T> setEquals(T equals) {
            this.equals = equals;
            return this;
        }

        public ComparableCurationRules.Builder<T> setNotEquals(T notEquals) {
            this.notEquals = notEquals;
            return this;
        }

        public ComparableCurationRules.Builder<T> setGreaterThan(T greaterThan) {
            this.greaterThan = greaterThan;
            return this;
        }

        public ComparableCurationRules.Builder<T> setLessThan(T lessThan) {
            this.lessThan = lessThan;
            return this;
        }

        public ComparableCurationRules.Builder<T> setGreaterThanOrEquals(T greaterThanOrEquals) {
            this.greaterThanOrEquals = greaterThanOrEquals;
            return this;
        }

        public ComparableCurationRules.Builder<T> setLessThanOrEquals(T lessThanOrEquals) {
            this.lessThanOrEquals = lessThanOrEquals;
            return this;
        }

        public ComparableCurationRules.Builder<T> setIn(Collection<T> in) {
            this.in = in;
            return this;
        }

        public ComparableCurationRules.Builder<T> setNotIn(Collection<T> notIn) {
            this.notIn = notIn;
            return this;
        }

        public ComparableCurationRules<T> build() {
            return new ComparableCurationRules<>(this);
        }
    }
}
