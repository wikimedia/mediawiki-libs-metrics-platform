package org.wikimedia.metrics_platform.curation.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class ComparableCurationRulesTest {

    @Test
    public void testIntegerGreaterThan() {
        ComparableCurationRules<Integer> rules = new ComparableCurationRules.Builder<Integer>()
                .setGreaterThan(0)
                .build();
        assertThat(rules.apply(1), is(true));
        assertThat(rules.apply(0), is(false));
        assertThat(rules.apply(-1), is(false));
    }

    @Test
    public void testFloatGreaterThan() {
        ComparableCurationRules<Float> rules = new ComparableCurationRules.Builder<Float>()
                .setGreaterThan(0.0f)
                .build();
        assertThat(rules.apply(1.0f), is(true));
        assertThat(rules.apply(0.0f), is(false));
        assertThat(rules.apply(-1.0f), is(false));
    }

    @Test
    public void testIntegerLessThan() {
        ComparableCurationRules<Integer> rules = new ComparableCurationRules.Builder<Integer>()
                .setLessThan(0)
                .build();
        assertThat(rules.apply(1), is(false));
        assertThat(rules.apply(0), is(false));
        assertThat(rules.apply(-1), is(true));
    }

    @Test
    public void testFloatLessThan() {
        ComparableCurationRules<Float> rules = new ComparableCurationRules.Builder<Float>()
                .setLessThan(0.0f)
                .build();
        assertThat(rules.apply(1.0f), is(false));
        assertThat(rules.apply(0.0f), is(false));
        assertThat(rules.apply(-1.0f), is(true));
    }

    @Test
    public void testIntegerGreaterThanOrEquals() {
        ComparableCurationRules<Integer> rules = new ComparableCurationRules.Builder<Integer>()
                .setGreaterThanOrEquals(0)
                .build();
        assertThat(rules.apply(1), is(true));
        assertThat(rules.apply(0), is(true));
        assertThat(rules.apply(-1), is(false));
    }

    @Test
    public void testFloatGreaterThanOrEquals() {
        ComparableCurationRules<Float> rules = new ComparableCurationRules.Builder<Float>()
                .setGreaterThanOrEquals(0.0f)
                .build();
        assertThat(rules.apply(1.0f), is(true));
        assertThat(rules.apply(0.0f), is(true));
        assertThat(rules.apply(-1.0f), is(false));
    }

    @Test
    public void testIntegerLessThanOrEquals() {
        ComparableCurationRules<Integer> rules = new ComparableCurationRules.Builder<Integer>()
                .setLessThanOrEquals(0)
                .build();
        assertThat(rules.apply(1), is(false));
        assertThat(rules.apply(0), is(true));
        assertThat(rules.apply(-1), is(true));
    }

    @Test
    public void testFloatLessThanOrEquals() {
        ComparableCurationRules<Float> rules = new ComparableCurationRules.Builder<Float>()
                .setLessThanOrEquals(0.0f)
                .build();
        assertThat(rules.apply(1.0f), is(false));
        assertThat(rules.apply(0.0f), is(true));
        assertThat(rules.apply(-1.0f), is(true));
    }

}
