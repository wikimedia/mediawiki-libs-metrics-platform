package org.wikimedia.metrics_platform.curation.rules;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ComparableCurationRulesTest {

    @Test
    public void testIntegerGreaterThan() {
        ComparableCurationRules<Integer> rules = ComparableCurationRules.<Integer>builder()
                .greaterThan(0)
                .build();
        assertThat(rules.apply(1)).isEqualTo(true);
        assertThat(rules.apply(0)).isEqualTo(false);
        assertThat(rules.apply(-1)).isEqualTo(false);
    }

    @Test
    public void testFloatGreaterThan() {
        ComparableCurationRules<Float> rules = ComparableCurationRules.<Float>builder()
                .greaterThan(0.0f)
                .build();
        assertThat(rules.apply(1.0f)).isEqualTo(true);
        assertThat(rules.apply(0.0f)).isEqualTo(false);
        assertThat(rules.apply(-1.0f)).isEqualTo(false);
    }

    @Test
    public void testIntegerLessThan() {
        ComparableCurationRules<Integer> rules = ComparableCurationRules.<Integer>builder()
                .lessThan(0)
                .build();
        assertThat(rules.apply(1)).isEqualTo(false);
        assertThat(rules.apply(0)).isEqualTo(false);
        assertThat(rules.apply(-1)).isEqualTo(true);
    }

    @Test
    public void testFloatLessThan() {
        ComparableCurationRules<Float> rules = ComparableCurationRules.<Float>builder()
                .lessThan(0.0f)
                .build();
        assertThat(rules.apply(1.0f)).isEqualTo(false);
        assertThat(rules.apply(0.0f)).isEqualTo(false);
        assertThat(rules.apply(-1.0f)).isEqualTo(true);
    }

    @Test
    public void testIntegerGreaterThanOrEquals() {
        ComparableCurationRules<Integer> rules = ComparableCurationRules.<Integer>builder()
                .greaterThanOrEquals(0)
                .build();
        assertThat(rules.apply(1)).isEqualTo(true);
        assertThat(rules.apply(0)).isEqualTo(true);
        assertThat(rules.apply(-1)).isEqualTo(false);
    }

    @Test
    public void testFloatGreaterThanOrEquals() {
        ComparableCurationRules<Float> rules = ComparableCurationRules.<Float>builder()
                .greaterThanOrEquals(0.0f)
                .build();
        assertThat(rules.apply(1.0f)).isEqualTo(true);
        assertThat(rules.apply(0.0f)).isEqualTo(true);
        assertThat(rules.apply(-1.0f)).isEqualTo(false);
    }

    @Test
    public void testIntegerLessThanOrEquals() {
        ComparableCurationRules<Integer> rules = ComparableCurationRules.<Integer>builder()
                .lessThanOrEquals(0)
                .build();
        assertThat(rules.apply(1)).isEqualTo(false);
        assertThat(rules.apply(0)).isEqualTo(true);
        assertThat(rules.apply(-1)).isEqualTo(true);
    }

    @Test
    public void testFloatLessThanOrEquals() {
        ComparableCurationRules<Float> rules = ComparableCurationRules.<Float>builder()
                .lessThanOrEquals(0.0f)
                .build();
        assertThat(rules.apply(1.0f)).isEqualTo(false);
        assertThat(rules.apply(0.0f)).isEqualTo(true);
        assertThat(rules.apply(-1.0f)).isEqualTo(true);
    }

}
