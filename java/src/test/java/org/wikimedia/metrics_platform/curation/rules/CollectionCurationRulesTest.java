package org.wikimedia.metrics_platform.curation.rules;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class CollectionCurationRulesTest {

    @Test void testIntegerCollectionContains() {
        CollectionCurationRules<Integer> rules = CollectionCurationRules.<Integer>builder()
                .contains(1)
                .build();
        assertThat(rules.apply(Arrays.asList(1, 2, 3))).isTrue();
        assertThat(rules.apply(Arrays.asList(3, 4, 5))).isFalse();
        assertThat(rules.apply(emptyList())).isFalse();
    }

    @Test void testFloatCollectionContains() {
        CollectionCurationRules<Float> rules = CollectionCurationRules.<Float>builder()
                .contains(1.0f)
                .build();
        assertThat(rules.apply(Arrays.asList(1.0f, 2.0f, 3.0f))).isTrue();
        assertThat(rules.apply(Arrays.asList(3.0f, 4.0f, 5.0f))).isFalse();
        assertThat(rules.apply(emptyList())).isFalse();
    }

    @Test void testStringCollectionContains() {
        CollectionCurationRules<String> rules = CollectionCurationRules.<String>builder()
                .contains("a")
                .build();
        assertThat(rules.apply(Arrays.asList("a", "b", "c"))).isTrue();
        assertThat(rules.apply(Arrays.asList("c", "d", "e"))).isFalse();
        assertThat(rules.apply(emptyList())).isFalse();
    }

    @Test void testIntegerCollectionDoesNotContain() {
        CollectionCurationRules<Integer> rules = CollectionCurationRules.<Integer>builder()
                .doesNotContain(1)
                .build();
        assertThat(rules.apply(Arrays.asList(1, 2, 3))).isFalse();
        assertThat(rules.apply(Arrays.asList(3, 4, 5))).isTrue();
        assertThat(rules.apply(emptyList())).isTrue();
    }

    @Test void testFloatCollectionDoesNotContain() {
        CollectionCurationRules<Float> rules = CollectionCurationRules.<Float>builder()
                .doesNotContain(1.0f)
                .build();
        assertThat(rules.apply(Arrays.asList(1.0f, 2.0f, 3.0f))).isFalse();
        assertThat(rules.apply(Arrays.asList(3.0f, 4.0f, 5.0f))).isTrue();
        assertThat(rules.apply(emptyList())).isTrue();
    }

    @Test void testStringCollectionDoesNotContain() {
        CollectionCurationRules<String> rules = CollectionCurationRules.<String>builder()
                .doesNotContain("a")
                .build();
        assertThat(rules.apply(Arrays.asList("a", "b", "c"))).isFalse();
        assertThat(rules.apply(Arrays.asList("c", "d", "e"))).isTrue();
        assertThat(rules.apply(emptyList())).isTrue();
    }

    @Test void testIntegerCollectionContainsAll() {
        CollectionCurationRules<Integer> rules = CollectionCurationRules.<Integer>builder()
                .containsAll(Arrays.asList(1, 2, 3))
                .build();
        assertThat(rules.apply(Arrays.asList(1, 2, 3))).isTrue();
        assertThat(rules.apply(Arrays.asList(3, 4, 5))).isFalse();
        assertThat(rules.apply(Arrays.asList(4, 5, 6))).isFalse();
        assertThat(rules.apply(emptyList())).isFalse();
    }

    @Test void testFloatCollectionContainsAll() {
        CollectionCurationRules<Float> rules = CollectionCurationRules.<Float>builder()
                .containsAll(Arrays.asList(1.0f, 2.0f, 3.0f))
                .build();
        assertThat(rules.apply(Arrays.asList(1.0f, 2.0f, 3.0f))).isTrue();
        assertThat(rules.apply(Arrays.asList(3.0f, 4.0f, 5.0f))).isFalse();
        assertThat(rules.apply(Arrays.asList(4.0f, 5.0f, 6.0f))).isFalse();
        assertThat(rules.apply(emptyList())).isFalse();
    }

    @Test void testStringCollectionContainsAll() {
        CollectionCurationRules<String> rules = CollectionCurationRules.<String>builder()
                .containsAll(Arrays.asList("a", "b", "c"))
                .build();
        assertThat(rules.apply(Arrays.asList("a", "b", "c"))).isTrue();
        assertThat(rules.apply(Arrays.asList("c", "d", "e"))).isFalse();
        assertThat(rules.apply(Arrays.asList("d", "e", "f"))).isFalse();
        assertThat(rules.apply(emptyList())).isFalse();
    }

    @Test void testIntegerCollectionContainsAny() {
        CollectionCurationRules<Integer> rules = CollectionCurationRules.<Integer>builder()
                .containsAny(Arrays.asList(1, 2, 3))
                .build();
        assertThat(rules.apply(Arrays.asList(1, 2, 3))).isTrue();
        assertThat(rules.apply(Arrays.asList(3, 4, 5))).isTrue();
        assertThat(rules.apply(Arrays.asList(4, 5, 6))).isFalse();
        assertThat(rules.apply(emptyList())).isFalse();
    }

    @Test void testFloatCollectionContainsAny() {
        CollectionCurationRules<Float> rules = CollectionCurationRules.<Float>builder()
                .containsAny(Arrays.asList(1.0f, 2.0f, 3.0f))
                .build();
        assertThat(rules.apply(Arrays.asList(1.0f, 2.0f, 3.0f))).isTrue();
        assertThat(rules.apply(Arrays.asList(3.0f, 4.0f, 5.0f))).isTrue();
        assertThat(rules.apply(Arrays.asList(4.0f, 5.0f, 6.0f))).isFalse();
        assertThat(rules.apply(emptyList())).isFalse();
    }

    @Test void testStringCollectionContainsAny() {
        CollectionCurationRules<String> rules = CollectionCurationRules.<String>builder()
                .containsAny(Arrays.asList("a", "b", "c"))
                .build();
        assertThat(rules.apply(Arrays.asList("a", "b", "c"))).isTrue();
        assertThat(rules.apply(Arrays.asList("c", "d", "e"))).isTrue();
        assertThat(rules.apply(Arrays.asList("d", "e", "f"))).isFalse();
        assertThat(rules.apply(emptyList())).isFalse();
    }
}
