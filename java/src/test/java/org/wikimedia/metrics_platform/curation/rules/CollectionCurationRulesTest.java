package org.wikimedia.metrics_platform.curation.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

public class CollectionCurationRulesTest {

    @Test
    public void testIntegerCollectionContains() {
        CollectionCurationRules<Integer> rules = new CollectionCurationRules.Builder<Integer>()
                .contains(1)
                .build();
        assertThat(rules.apply(Arrays.asList(1, 2, 3)), is(true));
        assertThat(rules.apply(Arrays.asList(3, 4, 5)), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testFloatCollectionContains() {
        CollectionCurationRules<Float> rules = new CollectionCurationRules.Builder<Float>()
                .contains(1.0f)
                .build();
        assertThat(rules.apply(Arrays.asList(1.0f, 2.0f, 3.0f)), is(true));
        assertThat(rules.apply(Arrays.asList(3.0f, 4.0f, 5.0f)), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testStringCollectionContains() {
        CollectionCurationRules<String> rules = new CollectionCurationRules.Builder<String>()
                .contains("a")
                .build();
        assertThat(rules.apply(Arrays.asList("a", "b", "c")), is(true));
        assertThat(rules.apply(Arrays.asList("c", "d", "e")), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testIntegerCollectionDoesNotContain() {
        CollectionCurationRules<Integer> rules = new CollectionCurationRules.Builder<Integer>()
                .doesNotContain(1)
                .build();
        assertThat(rules.apply(Arrays.asList(1, 2, 3)), is(false));
        assertThat(rules.apply(Arrays.asList(3, 4, 5)), is(true));
        assertThat(rules.apply(Collections.emptyList()), is(true));
    }

    @Test
    public void testFloatCollectionDoesNotContain() {
        CollectionCurationRules<Float> rules = new CollectionCurationRules.Builder<Float>()
                .doesNotContain(1.0f)
                .build();
        assertThat(rules.apply(Arrays.asList(1.0f, 2.0f, 3.0f)), is(false));
        assertThat(rules.apply(Arrays.asList(3.0f, 4.0f, 5.0f)), is(true));
        assertThat(rules.apply(Collections.emptyList()), is(true));
    }

    @Test
    public void testStringCollectionDoesNotContain() {
        CollectionCurationRules<String> rules = new CollectionCurationRules.Builder<String>()
                .doesNotContain("a")
                .build();
        assertThat(rules.apply(Arrays.asList("a", "b", "c")), is(false));
        assertThat(rules.apply(Arrays.asList("c", "d", "e")), is(true));
        assertThat(rules.apply(Collections.emptyList()), is(true));
    }

    @Test
    public void testIntegerCollectionContainsAll() {
        CollectionCurationRules<Integer> rules = new CollectionCurationRules.Builder<Integer>()
                .containsAll(Arrays.asList(1, 2, 3))
                .build();
        assertThat(rules.apply(Arrays.asList(1, 2, 3)), is(true));
        assertThat(rules.apply(Arrays.asList(3, 4, 5)), is(false));
        assertThat(rules.apply(Arrays.asList(4, 5, 6)), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testFloatCollectionContainsAll() {
        CollectionCurationRules<Float> rules = new CollectionCurationRules.Builder<Float>()
                .containsAll(Arrays.asList(1.0f, 2.0f, 3.0f))
                .build();
        assertThat(rules.apply(Arrays.asList(1.0f, 2.0f, 3.0f)), is(true));
        assertThat(rules.apply(Arrays.asList(3.0f, 4.0f, 5.0f)), is(false));
        assertThat(rules.apply(Arrays.asList(4.0f, 5.0f, 6.0f)), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testStringCollectionContainsAll() {
        CollectionCurationRules<String> rules = new CollectionCurationRules.Builder<String>()
                .containsAll(Arrays.asList("a", "b", "c"))
                .build();
        assertThat(rules.apply(Arrays.asList("a", "b", "c")), is(true));
        assertThat(rules.apply(Arrays.asList("c", "d", "e")), is(false));
        assertThat(rules.apply(Arrays.asList("d", "e", "f")), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testIntegerCollectionContainsAny() {
        CollectionCurationRules<Integer> rules = new CollectionCurationRules.Builder<Integer>()
                .containsAny(Arrays.asList(1, 2, 3))
                .build();
        assertThat(rules.apply(Arrays.asList(1, 2, 3)), is(true));
        assertThat(rules.apply(Arrays.asList(3, 4, 5)), is(true));
        assertThat(rules.apply(Arrays.asList(4, 5, 6)), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testFloatCollectionContainsAny() {
        CollectionCurationRules<Float> rules = new CollectionCurationRules.Builder<Float>()
                .containsAny(Arrays.asList(1.0f, 2.0f, 3.0f))
                .build();
        assertThat(rules.apply(Arrays.asList(1.0f, 2.0f, 3.0f)), is(true));
        assertThat(rules.apply(Arrays.asList(3.0f, 4.0f, 5.0f)), is(true));
        assertThat(rules.apply(Arrays.asList(4.0f, 5.0f, 6.0f)), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testStringCollectionContainsAny() {
        CollectionCurationRules<String> rules = new CollectionCurationRules.Builder<String>()
                .containsAny(Arrays.asList("a", "b", "c"))
                .build();
        assertThat(rules.apply(Arrays.asList("a", "b", "c")), is(true));
        assertThat(rules.apply(Arrays.asList("c", "d", "e")), is(true));
        assertThat(rules.apply(Arrays.asList("d", "e", "f")), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

}
