package org.wikimedia.metrics_platform.curation.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class CurationRulesTest {

    @Test
    public void testIntegerEquals() {
        CurationRules<Integer> rules = CurationRules.<Integer>builder().isEquals(1).build();
        assertThat(rules.apply(1), is(true));
        assertThat(rules.apply(0), is(false));
        assertThat(rules.apply(-1), is(false));
        assertThat(rules.apply(null), is(false));
    }

    @Test
    public void testFloatEquals() {
        CurationRules<Float> rules = CurationRules.<Float>builder().isEquals(1.0f).build();
        assertThat(rules.apply(1.0f), is(true));
        assertThat(rules.apply(0.0f), is(false));
        assertThat(rules.apply(-1.0f), is(false));
        assertThat(rules.apply(null), is(false));
    }

    @Test
    public void testStringEquals() {
        CurationRules<String> rules = CurationRules.<String>builder().isEquals("foo").build();
        assertThat(rules.apply("foo"), is(true));
        assertThat(rules.apply("bar"), is(false));
        assertThat(rules.apply(""), is(false));
        assertThat(rules.apply(null), is(false));
    }

    @Test
    public void testBooleanEquals() {
        CurationRules<Boolean> rules = CurationRules.<Boolean>builder().isEquals(true).build();
        assertThat(rules.apply(true), is(true));
        assertThat(rules.apply(false), is(false));
        assertThat(rules.apply(null), is(false));
    }

    @Test
    public void testIntegerNotEquals() {
        CurationRules<Integer> rules = CurationRules.<Integer>builder().notEquals(1).build();
        assertThat(rules.apply(1), is(false));
        assertThat(rules.apply(0), is(true));
        assertThat(rules.apply(-1), is(true));
        assertThat(rules.apply(null), is(true));
    }

    @Test
    public void testFloatNotEquals() {
        CurationRules<Float> rules = CurationRules.<Float>builder().notEquals(1.0f).build();
        assertThat(rules.apply(1.0f), is(false));
        assertThat(rules.apply(0.0f), is(true));
        assertThat(rules.apply(-1.0f), is(true));
        assertThat(rules.apply(null), is(true));
    }

    @Test
    public void testStringNotEquals() {
        CurationRules<String> rules = CurationRules.<String>builder().notEquals("foo").build();
        assertThat(rules.apply("foo"), is(false));
        assertThat(rules.apply("bar"), is(true));
        assertThat(rules.apply(""), is(true));
        assertThat(rules.apply(null), is(true));
    }

    @Test
    public void testBooleanNotEquals() {
        CurationRules<Boolean> rules = CurationRules.<Boolean>builder().notEquals(true).build();
        assertThat(rules.apply(true), is(false));
        assertThat(rules.apply(false), is(true));
        assertThat(rules.apply(null), is(true));
    }

    @Test
    public void testIntegerInCollection() {
        CurationRules<Integer> rules = CurationRules.<Integer>builder().in(Arrays.asList(1, 2, 3)).build();
        assertThat(rules.apply(1), is(true));
        assertThat(rules.apply(4), is(false));
        assertThat(rules.apply(-1), is(false));
        assertThat(rules.apply(null), is(false));
    }

    @Test
    public void testFloatInCollection() {
        CurationRules<Float> rules = CurationRules.<Float>builder().in(Arrays.asList(1.0f, 2.0f, 3.0f)).build();
        assertThat(rules.apply(1.0f), is(true));
        assertThat(rules.apply(4.0f), is(false));
        assertThat(rules.apply(-1.0f), is(false));
        assertThat(rules.apply(null), is(false));
    }

    @Test
    public void testStringInCollection() {
        CurationRules<String> rules = CurationRules.<String>builder().in(Arrays.asList("a", "b", "c")).build();
        assertThat(rules.apply("a"), is(true));
        assertThat(rules.apply("d"), is(false));
        assertThat(rules.apply(""), is(false));
        assertThat(rules.apply(null), is(false));
    }

    @Test
    public void testIntegerNotInCollection() {
        CurationRules<Integer> rules = CurationRules.<Integer>builder().notIn(Arrays.asList(1, 2, 3)).build();
        assertThat(rules.apply(1), is(false));
        assertThat(rules.apply(4), is(true));
        assertThat(rules.apply(-1), is(true));
        assertThat(rules.apply(null), is(true));
    }

    @Test
    public void testFloaNotInCollection() {
        CurationRules<Float> rules = CurationRules.<Float>builder()
                .notIn(Arrays.asList(1.0f, 2.0f, 3.0f))
                .build();
        assertThat(rules.apply(1.0f), is(false));
        assertThat(rules.apply(4.0f), is(true));
        assertThat(rules.apply(-1.0f), is(true));
        assertThat(rules.apply(null), is(true));
    }

    @Test
    public void testStringNotInCollection() {
        CurationRules<String> rules = CurationRules.<String>builder()
                .notIn(Arrays.asList("a", "b", "c"))
                .build();
        assertThat(rules.apply("a"), is(false));
        assertThat(rules.apply("d"), is(true));
        assertThat(rules.apply(""), is(true));
        assertThat(rules.apply(null), is(true));
    }

}
