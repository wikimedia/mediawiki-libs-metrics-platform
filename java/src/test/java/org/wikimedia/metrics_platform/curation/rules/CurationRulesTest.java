package org.wikimedia.metrics_platform.curation.rules;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class CurationRulesTest {

    @Test
    public void testIntegerEquals() {
        CurationRules<Integer> rules = CurationRules.<Integer>builder().isEquals(1).build();
        assertThat(rules.apply(1)).isEqualTo(true);
        assertThat(rules.apply(0)).isEqualTo(false);
        assertThat(rules.apply(-1)).isEqualTo(false);
        assertThat(rules.apply(null)).isEqualTo(false);
    }

    @Test
    public void testFloatEquals() {
        CurationRules<Float> rules = CurationRules.<Float>builder().isEquals(1.0f).build();
        assertThat(rules.apply(1.0f)).isEqualTo(true);
        assertThat(rules.apply(0.0f)).isEqualTo(false);
        assertThat(rules.apply(-1.0f)).isEqualTo(false);
        assertThat(rules.apply(null)).isEqualTo(false);
    }

    @Test
    public void testStringEquals() {
        CurationRules<String> rules = CurationRules.<String>builder().isEquals("foo").build();
        assertThat(rules.apply("foo")).isEqualTo(true);
        assertThat(rules.apply("bar")).isEqualTo(false);
        assertThat(rules.apply("")).isEqualTo(false);
        assertThat(rules.apply(null)).isEqualTo(false);
    }

    @Test
    public void testBooleanEquals() {
        CurationRules<Boolean> rules = CurationRules.<Boolean>builder().isEquals(true).build();
        assertThat(rules.apply(true)).isEqualTo(true);
        assertThat(rules.apply(false)).isEqualTo(false);
        assertThat(rules.apply(null)).isEqualTo(false);
    }

    @Test
    public void testIntegerNotEquals() {
        CurationRules<Integer> rules = CurationRules.<Integer>builder().notEquals(1).build();
        assertThat(rules.apply(1)).isEqualTo(false);
        assertThat(rules.apply(0)).isEqualTo(true);
        assertThat(rules.apply(-1)).isEqualTo(true);
        assertThat(rules.apply(null)).isEqualTo(true);
    }

    @Test
    public void testFloatNotEquals() {
        CurationRules<Float> rules = CurationRules.<Float>builder().notEquals(1.0f).build();
        assertThat(rules.apply(1.0f)).isEqualTo(false);
        assertThat(rules.apply(0.0f)).isEqualTo(true);
        assertThat(rules.apply(-1.0f)).isEqualTo(true);
        assertThat(rules.apply(null)).isEqualTo(true);
    }

    @Test
    public void testStringNotEquals() {
        CurationRules<String> rules = CurationRules.<String>builder().notEquals("foo").build();
        assertThat(rules.apply("foo")).isEqualTo(false);
        assertThat(rules.apply("bar")).isEqualTo(true);
        assertThat(rules.apply("")).isEqualTo(true);
        assertThat(rules.apply(null)).isEqualTo(true);
    }

    @Test
    public void testBooleanNotEquals() {
        CurationRules<Boolean> rules = CurationRules.<Boolean>builder().notEquals(true).build();
        assertThat(rules.apply(true)).isEqualTo(false);
        assertThat(rules.apply(false)).isEqualTo(true);
        assertThat(rules.apply(null)).isEqualTo(true);
    }

    @Test
    public void testIntegerInCollection() {
        CurationRules<Integer> rules = CurationRules.<Integer>builder().in(Arrays.asList(1, 2, 3)).build();
        assertThat(rules.apply(1)).isEqualTo(true);
        assertThat(rules.apply(4)).isEqualTo(false);
        assertThat(rules.apply(-1)).isEqualTo(false);
        assertThat(rules.apply(null)).isEqualTo(false);
    }

    @Test
    public void testFloatInCollection() {
        CurationRules<Float> rules = CurationRules.<Float>builder().in(Arrays.asList(1.0f, 2.0f, 3.0f)).build();
        assertThat(rules.apply(1.0f)).isEqualTo(true);
        assertThat(rules.apply(4.0f)).isEqualTo(false);
        assertThat(rules.apply(-1.0f)).isEqualTo(false);
        assertThat(rules.apply(null)).isEqualTo(false);
    }

    @Test
    public void testStringInCollection() {
        CurationRules<String> rules = CurationRules.<String>builder().in(Arrays.asList("a", "b", "c")).build();
        assertThat(rules.apply("a")).isEqualTo(true);
        assertThat(rules.apply("d")).isEqualTo(false);
        assertThat(rules.apply("")).isEqualTo(false);
        assertThat(rules.apply(null)).isEqualTo(false);
    }

    @Test
    public void testIntegerNotInCollection() {
        CurationRules<Integer> rules = CurationRules.<Integer>builder().notIn(Arrays.asList(1, 2, 3)).build();
        assertThat(rules.apply(1)).isEqualTo(false);
        assertThat(rules.apply(4)).isEqualTo(true);
        assertThat(rules.apply(-1)).isEqualTo(true);
        assertThat(rules.apply(null)).isEqualTo(true);
    }

    @Test
    public void testFloaNotInCollection() {
        CurationRules<Float> rules = CurationRules.<Float>builder()
                .notIn(Arrays.asList(1.0f, 2.0f, 3.0f))
                .build();
        assertThat(rules.apply(1.0f)).isEqualTo(false);
        assertThat(rules.apply(4.0f)).isEqualTo(true);
        assertThat(rules.apply(-1.0f)).isEqualTo(true);
        assertThat(rules.apply(null)).isEqualTo(true);
    }

    @Test
    public void testStringNotInCollection() {
        CurationRules<String> rules = CurationRules.<String>builder()
                .notIn(Arrays.asList("a", "b", "c"))
                .build();
        assertThat(rules.apply("a")).isEqualTo(false);
        assertThat(rules.apply("d")).isEqualTo(true);
        assertThat(rules.apply("")).isEqualTo(true);
        assertThat(rules.apply(null)).isEqualTo(true);
    }

}
