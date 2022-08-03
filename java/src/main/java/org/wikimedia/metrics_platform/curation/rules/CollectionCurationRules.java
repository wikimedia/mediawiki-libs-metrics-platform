package org.wikimedia.metrics_platform.curation.rules;

import java.util.Collection;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder @EqualsAndHashCode @ToString
public class CollectionCurationRules<T> {
    private T contains;
    @SerializedName("does_not_contain") private T doesNotContain;
    @SerializedName("contains_all") private Collection<T> containsAll;
    @SerializedName("contains_any") private Collection<T> containsAny;

    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public boolean apply(Collection<T> value) {
        if (contains != null && !value.contains(contains)) {
            return false;
        }
        if (doesNotContain != null && value.contains(doesNotContain)) {
            return false;
        }
        if (containsAll != null && !value.containsAll(containsAll)) {
            return false;
        }
        if (containsAny != null) {
            boolean found = false;
            for (T el : containsAny) {
                if (value.contains(el)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
}
