package org.wikimedia.metrics_platform.curation.rules;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class CurationRulesTest {

    @Test void testIntegerEquals() {
        CurationRules<Integer> rules = CurationRules.<Integer>builder().isEquals(1).build();
        assertThat(rules.apply(1)).isTrue();
        assertThat(rules.apply(0)).isFalse();
        assertThat(rules.apply(-1)).isFalse();
        assertThat(rules.apply(null)).isFalse();
    }

    @Test void testFloatEquals() {
        CurationRules<Float> rules = CurationRules.<Float>builder().isEquals(1.0f).build();
        assertThat(rules.apply(1.0f)).isTrue();
        assertThat(rules.apply(0.0f)).isFalse();
        assertThat(rules.apply(-1.0f)).isFalse();
        assertThat(rules.apply(null)).isFalse();
    }

    @Test void testStringEquals() {
        CurationRules<String> rules = CurationRules.<String>builder().isEquals("foo").build();
        assertThat(rules.apply("foo")).isTrue();
        assertThat(rules.apply("bar")).isFalse();
        assertThat(rules.apply("")).isFalse();
        assertThat(rules.apply(null)).isFalse();
    }

    @Test void testBooleanEquals() {
        CurationRules<Boolean> rules = CurationRules.<Boolean>builder().isEquals(true).build();
        assertThat(rules.apply(true)).isTrue();
        assertThat(rules.apply(false)).isFalse();
        assertThat(rules.apply(null)).isFalse();
    }

    @Test void testIntegerNotEquals() {
        CurationRules<Integer> rules = CurationRules.<Integer>builder().notEquals(1).build();
        assertThat(rules.apply(1)).isFalse();
        assertThat(rules.apply(0)).isTrue();
        assertThat(rules.apply(-1)).isTrue();
        assertThat(rules.apply(null)).isTrue();
    }

    @Test void testFloatNotEquals() {
        CurationRules<Float> rules = CurationRules.<Float>builder().notEquals(1.0f).build();
        assertThat(rules.apply(1.0f)).isFalse();
        assertThat(rules.apply(0.0f)).isTrue();
        assertThat(rules.apply(-1.0f)).isTrue();
        assertThat(rules.apply(null)).isTrue();
    }

    @Test void testStringNotEquals() {
        CurationRules<String> rules = CurationRules.<String>builder().notEquals("foo").build();
        assertThat(rules.apply("foo")).isFalse();
        assertThat(rules.apply("bar")).isTrue();
        assertThat(rules.apply("")).isTrue();
        assertThat(rules.apply(null)).isTrue();
    }

    @Test void testBooleanNotEquals() {
        CurationRules<Boolean> rules = CurationRules.<Boolean>builder().notEquals(true).build();
        assertThat(rules.apply(true)).isFalse();
        assertThat(rules.apply(false)).isTrue();
        assertThat(rules.apply(null)).isTrue();
    }

    @Test void testIntegerInCollection() {
        CurationRules<Integer> rules = CurationRules.<Integer>builder().in(Arrays.asList(1, 2, 3)).build();
        assertThat(rules.apply(1)).isTrue();
        assertThat(rules.apply(4)).isFalse();
        assertThat(rules.apply(-1)).isFalse();
        assertThat(rules.apply(null)).isFalse();
    }

    @Test void testFloatInCollection() {
        CurationRules<Float> rules = CurationRules.<Float>builder().in(Arrays.asList(1.0f, 2.0f, 3.0f)).build();
        assertThat(rules.apply(1.0f)).isTrue();
        assertThat(rules.apply(4.0f)).isFalse();
        assertThat(rules.apply(-1.0f)).isFalse();
        assertThat(rules.apply(null)).isFalse();
    }

    @Test void testStringInCollection() {
        CurationRules<String> rules = CurationRules.<String>builder().in(Arrays.asList("a", "b", "c")).build();
        assertThat(rules.apply("a")).isTrue();
        assertThat(rules.apply("d")).isFalse();
        assertThat(rules.apply("")).isFalse();
        assertThat(rules.apply(null)).isFalse();
    }

    @Test void testIntegerNotInCollection() {
        CurationRules<Integer> rules = CurationRules.<Integer>builder().notIn(Arrays.asList(1, 2, 3)).build();
        assertThat(rules.apply(1)).isFalse();
        assertThat(rules.apply(4)).isTrue();
        assertThat(rules.apply(-1)).isTrue();
        assertThat(rules.apply(null)).isTrue();
    }

    @Test void testFloaNotInCollection() {
        CurationRules<Float> rules = CurationRules.<Float>builder()
                .notIn(Arrays.asList(1.0f, 2.0f, 3.0f))
                .build();
        assertThat(rules.apply(1.0f)).isFalse();
        assertThat(rules.apply(4.0f)).isTrue();
        assertThat(rules.apply(-1.0f)).isTrue();
        assertThat(rules.apply(null)).isTrue();
    }

    @Test void testStringNotInCollection() {
        CurationRules<String> rules = CurationRules.<String>builder()
                .notIn(Arrays.asList("a", "b", "c"))
                .build();
        assertThat(rules.apply("a")).isFalse();
        assertThat(rules.apply("d")).isTrue();
        assertThat(rules.apply("")).isTrue();
        assertThat(rules.apply(null)).isTrue();
    }
}
