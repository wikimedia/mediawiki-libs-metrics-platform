package org.wikimedia.metrics_platform.curation.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

public class CollectionCurationRulesTest {

    @Test
    public void testIntegerCollectionContains() {
        CollectionCurationRules<Integer> rules = CollectionCurationRules.<Integer>builder()
                .contains(1)
                .build();
        assertThat(rules.apply(Arrays.asList(1, 2, 3)), is(true));
        assertThat(rules.apply(Arrays.asList(3, 4, 5)), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testFloatCollectionContains() {
        CollectionCurationRules<Float> rules = CollectionCurationRules.<Float>builder()
                .contains(1.0f)
                .build();
        assertThat(rules.apply(Arrays.asList(1.0f, 2.0f, 3.0f)), is(true));
        assertThat(rules.apply(Arrays.asList(3.0f, 4.0f, 5.0f)), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testStringCollectionContains() {
        CollectionCurationRules<String> rules = CollectionCurationRules.<String>builder()
                .contains("a")
                .build();
        assertThat(rules.apply(Arrays.asList("a", "b", "c")), is(true));
        assertThat(rules.apply(Arrays.asList("c", "d", "e")), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testIntegerCollectionDoesNotContain() {
        CollectionCurationRules<Integer> rules = CollectionCurationRules.<Integer>builder()
                .doesNotContain(1)
                .build();
        assertThat(rules.apply(Arrays.asList(1, 2, 3)), is(false));
        assertThat(rules.apply(Arrays.asList(3, 4, 5)), is(true));
        assertThat(rules.apply(Collections.emptyList()), is(true));
    }

    @Test
    public void testFloatCollectionDoesNotContain() {
        CollectionCurationRules<Float> rules = CollectionCurationRules.<Float>builder()
                .doesNotContain(1.0f)
                .build();
        assertThat(rules.apply(Arrays.asList(1.0f, 2.0f, 3.0f)), is(false));
        assertThat(rules.apply(Arrays.asList(3.0f, 4.0f, 5.0f)), is(true));
        assertThat(rules.apply(Collections.emptyList()), is(true));
    }

    @Test
    public void testStringCollectionDoesNotContain() {
        CollectionCurationRules<String> rules = CollectionCurationRules.<String>builder()
                .doesNotContain("a")
                .build();
        assertThat(rules.apply(Arrays.asList("a", "b", "c")), is(false));
        assertThat(rules.apply(Arrays.asList("c", "d", "e")), is(true));
        assertThat(rules.apply(Collections.emptyList()), is(true));
    }

    @Test
    public void testIntegerCollectionContainsAll() {
        CollectionCurationRules<Integer> rules = CollectionCurationRules.<Integer>builder()
                .containsAll(Arrays.asList(1, 2, 3))
                .build();
        assertThat(rules.apply(Arrays.asList(1, 2, 3)), is(true));
        assertThat(rules.apply(Arrays.asList(3, 4, 5)), is(false));
        assertThat(rules.apply(Arrays.asList(4, 5, 6)), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testFloatCollectionContainsAll() {
        CollectionCurationRules<Float> rules = CollectionCurationRules.<Float>builder()
                .containsAll(Arrays.asList(1.0f, 2.0f, 3.0f))
                .build();
        assertThat(rules.apply(Arrays.asList(1.0f, 2.0f, 3.0f)), is(true));
        assertThat(rules.apply(Arrays.asList(3.0f, 4.0f, 5.0f)), is(false));
        assertThat(rules.apply(Arrays.asList(4.0f, 5.0f, 6.0f)), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testStringCollectionContainsAll() {
        CollectionCurationRules<String> rules = CollectionCurationRules.<String>builder()
                .containsAll(Arrays.asList("a", "b", "c"))
                .build();
        assertThat(rules.apply(Arrays.asList("a", "b", "c")), is(true));
        assertThat(rules.apply(Arrays.asList("c", "d", "e")), is(false));
        assertThat(rules.apply(Arrays.asList("d", "e", "f")), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testIntegerCollectionContainsAny() {
        CollectionCurationRules<Integer> rules = CollectionCurationRules.<Integer>builder()
                .containsAny(Arrays.asList(1, 2, 3))
                .build();
        assertThat(rules.apply(Arrays.asList(1, 2, 3)), is(true));
        assertThat(rules.apply(Arrays.asList(3, 4, 5)), is(true));
        assertThat(rules.apply(Arrays.asList(4, 5, 6)), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testFloatCollectionContainsAny() {
        CollectionCurationRules<Float> rules = CollectionCurationRules.<Float>builder()
                .containsAny(Arrays.asList(1.0f, 2.0f, 3.0f))
                .build();
        assertThat(rules.apply(Arrays.asList(1.0f, 2.0f, 3.0f)), is(true));
        assertThat(rules.apply(Arrays.asList(3.0f, 4.0f, 5.0f)), is(true));
        assertThat(rules.apply(Arrays.asList(4.0f, 5.0f, 6.0f)), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

    @Test
    public void testStringCollectionContainsAny() {
        CollectionCurationRules<String> rules = CollectionCurationRules.<String>builder()
                .containsAny(Arrays.asList("a", "b", "c"))
                .build();
        assertThat(rules.apply(Arrays.asList("a", "b", "c")), is(true));
        assertThat(rules.apply(Arrays.asList("c", "d", "e")), is(true));
        assertThat(rules.apply(Arrays.asList("d", "e", "f")), is(false));
        assertThat(rules.apply(Collections.emptyList()), is(false));
    }

}
