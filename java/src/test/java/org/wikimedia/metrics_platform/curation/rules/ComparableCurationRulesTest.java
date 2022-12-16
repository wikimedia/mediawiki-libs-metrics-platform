package org.wikimedia.metrics_platform.curation.rules;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ComparableCurationRulesTest {

    @Test void testIntegerGreaterThan() {
        ComparableCurationRules<Integer> rules = ComparableCurationRules.<Integer>builder()
                .greaterThan(0)
                .build();
        assertThat(rules.apply(1)).isTrue();
        assertThat(rules.apply(0)).isFalse();
        assertThat(rules.apply(-1)).isFalse();
    }

    @Test void testFloatGreaterThan() {
        ComparableCurationRules<Float> rules = ComparableCurationRules.<Float>builder()
                .greaterThan(0.0f)
                .build();
        assertThat(rules.apply(1.0f)).isTrue();
        assertThat(rules.apply(0.0f)).isFalse();
        assertThat(rules.apply(-1.0f)).isFalse();
    }

    @Test void testIntegerLessThan() {
        ComparableCurationRules<Integer> rules = ComparableCurationRules.<Integer>builder()
                .lessThan(0)
                .build();
        assertThat(rules.apply(1)).isFalse();
        assertThat(rules.apply(0)).isFalse();
        assertThat(rules.apply(-1)).isTrue();
    }

    @Test void testFloatLessThan() {
        ComparableCurationRules<Float> rules = ComparableCurationRules.<Float>builder()
                .lessThan(0.0f)
                .build();
        assertThat(rules.apply(1.0f)).isFalse();
        assertThat(rules.apply(0.0f)).isFalse();
        assertThat(rules.apply(-1.0f)).isTrue();
    }

    @Test void testIntegerGreaterThanOrEquals() {
        ComparableCurationRules<Integer> rules = ComparableCurationRules.<Integer>builder()
                .greaterThanOrEquals(0)
                .build();
        assertThat(rules.apply(1)).isTrue();
        assertThat(rules.apply(0)).isTrue();
        assertThat(rules.apply(-1)).isFalse();
    }

    @Test void testFloatGreaterThanOrEquals() {
        ComparableCurationRules<Float> rules = ComparableCurationRules.<Float>builder()
                .greaterThanOrEquals(0.0f)
                .build();
        assertThat(rules.apply(1.0f)).isTrue();
        assertThat(rules.apply(0.0f)).isTrue();
        assertThat(rules.apply(-1.0f)).isFalse();
    }

    @Test void testIntegerLessThanOrEquals() {
        ComparableCurationRules<Integer> rules = ComparableCurationRules.<Integer>builder()
                .lessThanOrEquals(0)
                .build();
        assertThat(rules.apply(1)).isFalse();
        assertThat(rules.apply(0)).isTrue();
        assertThat(rules.apply(-1)).isTrue();
    }

    @Test void testFloatLessThanOrEquals() {
        ComparableCurationRules<Float> rules = ComparableCurationRules.<Float>builder()
                .lessThanOrEquals(0.0f)
                .build();
        assertThat(rules.apply(1.0f)).isFalse();
        assertThat(rules.apply(0.0f)).isTrue();
        assertThat(rules.apply(-1.0f)).isTrue();
    }
}
