package org.wikimedia.metrics_platform.curation.rules;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CurationRulesTest {

    @Test
    public void testIntegerEquals() {
        CurationRules<Integer> rules = new CurationRules.Builder<Integer>().setEquals(1).build();
        assertThat(rules.apply(1), is(true));
        assertThat(rules.apply(0), is(false));
        assertThat(rules.apply(-1), is(false));
        assertThat(rules.apply(null), is(false));
    }

    @Test
    public void testFloatEquals() {
        CurationRules<Float> rules = new CurationRules.Builder<Float>().setEquals(1.0f).build();
        assertThat(rules.apply(1.0f), is(true));
        assertThat(rules.apply(0.0f), is(false));
        assertThat(rules.apply(-1.0f), is(false));
        assertThat(rules.apply(null), is(false));
    }

    @Test
    public void testStringEquals() {
        CurationRules<String> rules = new CurationRules.Builder<String>().setEquals("foo").build();
        assertThat(rules.apply("foo"), is(true));
        assertThat(rules.apply("bar"), is(false));
        assertThat(rules.apply(""), is(false));
        assertThat(rules.apply(null), is(false));
    }

    @Test
    public void testBooleanEquals() {
        CurationRules<Boolean> rules = new CurationRules.Builder<Boolean>().setEquals(true).build();
        assertThat(rules.apply(true), is(true));
        assertThat(rules.apply(false), is(false));
        assertThat(rules.apply(null), is(false));
    }

    @Test
    public void testIntegerNotEquals() {
        CurationRules<Integer> rules = new CurationRules.Builder<Integer>().setNotEquals(1).build();
        assertThat(rules.apply(1), is(false));
        assertThat(rules.apply(0), is(true));
        assertThat(rules.apply(-1), is(true));
        assertThat(rules.apply(null), is(true));
    }

    @Test
    public void testFloatNotEquals() {
        CurationRules<Float> rules = new CurationRules.Builder<Float>().setNotEquals(1.0f).build();
        assertThat(rules.apply(1.0f), is(false));
        assertThat(rules.apply(0.0f), is(true));
        assertThat(rules.apply(-1.0f), is(true));
        assertThat(rules.apply(null), is(true));
    }

    @Test
    public void testStringNotEquals() {
        CurationRules<String> rules = new CurationRules.Builder<String>().setNotEquals("foo").build();
        assertThat(rules.apply("foo"), is(false));
        assertThat(rules.apply("bar"), is(true));
        assertThat(rules.apply(""), is(true));
        assertThat(rules.apply(null), is(true));
    }

    @Test
    public void testBooleanNotEquals() {
        CurationRules<Boolean> rules = new CurationRules.Builder<Boolean>().setNotEquals(true).build();
        assertThat(rules.apply(true), is(false));
        assertThat(rules.apply(false), is(true));
        assertThat(rules.apply(null), is(true));
    }

    @Test
    public void testIntegerInCollection() {
        CurationRules<Integer> rules = new CurationRules.Builder<Integer>().setIn(Arrays.asList(1, 2, 3)).build();
        assertThat(rules.apply(1), is(true));
        assertThat(rules.apply(4), is(false));
        assertThat(rules.apply(-1), is(false));
        assertThat(rules.apply(null), is(false));
    }

    @Test
    public void testFloatInCollection() {
        CurationRules<Float> rules = new CurationRules.Builder<Float>().setIn(Arrays.asList(1.0f, 2.0f, 3.0f)).build();
        assertThat(rules.apply(1.0f), is(true));
        assertThat(rules.apply(4.0f), is(false));
        assertThat(rules.apply(-1.0f), is(false));
        assertThat(rules.apply(null), is(false));
    }

    @Test
    public void testStringInCollection() {
        CurationRules<String> rules = new CurationRules.Builder<String>().setIn(Arrays.asList("a", "b", "c")).build();
        assertThat(rules.apply("a"), is(true));
        assertThat(rules.apply("d"), is(false));
        assertThat(rules.apply(""), is(false));
        assertThat(rules.apply(null), is(false));
    }

    @Test
    public void testIntegerNotInCollection() {
        CurationRules<Integer> rules = new CurationRules.Builder<Integer>().setNotIn(Arrays.asList(1, 2, 3)).build();
        assertThat(rules.apply(1), is(false));
        assertThat(rules.apply(4), is(true));
        assertThat(rules.apply(-1), is(true));
        assertThat(rules.apply(null), is(true));
    }

    @Test
    public void testFloaNotInCollection() {
        CurationRules<Float> rules = new CurationRules.Builder<Float>()
                .setNotIn(Arrays.asList(1.0f, 2.0f, 3.0f))
                .build();
        assertThat(rules.apply(1.0f), is(false));
        assertThat(rules.apply(4.0f), is(true));
        assertThat(rules.apply(-1.0f), is(true));
        assertThat(rules.apply(null), is(true));
    }

    @Test
    public void testStringNotInCollection() {
        CurationRules<String> rules = new CurationRules.Builder<String>()
                .setNotIn(Arrays.asList("a", "b", "c"))
                .build();
        assertThat(rules.apply("a"), is(false));
        assertThat(rules.apply("d"), is(true));
        assertThat(rules.apply(""), is(true));
        assertThat(rules.apply(null), is(true));
    }

}
