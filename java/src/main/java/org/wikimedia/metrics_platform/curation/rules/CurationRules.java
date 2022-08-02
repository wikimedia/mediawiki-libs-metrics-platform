package org.wikimedia.metrics_platform.curation.rules;

import java.util.Collection;

import com.google.gson.annotations.SerializedName;

public class CurationRules<T> {
    private T equals;
    @SerializedName("not_equals") private T notEquals;
    private Collection<T> in;
    @SerializedName("not_in") private Collection<T> notIn;

    public CurationRules(Builder<T> builder) {
        this.equals = builder.equals;
        this.notEquals = builder.notEquals;
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
        if (in != null && !in.contains(value)) {
            return false;
        }
        if (notIn != null && notIn.contains(value)) {
            return false;
        }
        return true;
    }

    public static class Builder<T> {
        private T equals;
        private T notEquals;
        private Collection<T> in;
        private Collection<T> notIn;

        public Builder<T> setEquals(T equals) {
            this.equals = equals;
            return this;
        }

        public Builder<T> setNotEquals(T notEquals) {
            this.notEquals = notEquals;
            return this;
        }

        public Builder<T> setIn(Collection<T> in) {
            this.in = in;
            return this;
        }

        public Builder<T> setNotIn(Collection<T> notIn) {
            this.notIn = notIn;
            return this;
        }

        public CurationRules<T> build() {
            return new CurationRules<>(this);
        }
    }
}
