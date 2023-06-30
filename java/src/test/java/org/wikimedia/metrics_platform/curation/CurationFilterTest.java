package org.wikimedia.metrics_platform.curation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.wikimedia.metrics_platform.event.EventFixtures.getEvent;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.context.PerformerData;
import org.wikimedia.metrics_platform.json.GsonHelper;
import org.wikimedia.metrics_platform.config.CurationFilter;
import org.wikimedia.metrics_platform.event.EventProcessed;

import com.google.gson.Gson;

class CurationFilterTest {

    private static CurationFilter curationFilter;

    private static final List<String> groups = Arrays.asList("user", "autoconfirmed", "steward");

    @BeforeAll static void setUp() {
        Gson gson = GsonHelper.getGson();
        String curationFilterJson = "{\"page_id\":{\"less_than\":500,\"not_equals\":42},\"page_namespace_name\":" +
                "{\"equals\":\"Talk\"},\"performer_is_logged_in\":{\"equals\":true},\"performer_edit_count_bucket\":" +
                "{\"in\":[\"100-999 edits\",\"1000+ edits\"]},\"performer_groups\":{\"contains_all\":" +
                "[\"user\",\"autoconfirmed\"],\"does_not_contain\":\"sysop\"}}";
        curationFilter = gson.fromJson(curationFilterJson, CurationFilter.class);
    }

    @Test void testEventPasses() {
        assertThat(curationFilter.apply(getEvent())).isTrue();
    }

    @Test void testEventFailsWrongPageId() {
        EventProcessed event = getEvent(42, "Talk", groups, true, "1000+ edits");
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsWrongPageNamespaceText() {
        EventProcessed event = getEvent(1, "User", groups, true, "1000+ edits");
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsWrongUserGroups() {
        List<String> wrongGroups = Arrays.asList("user", "autoconfirmed", "sysop");
        EventProcessed event = getEvent(1, "Talk", wrongGroups, true, "1000+ edits");
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsNoUserGroups() {
        EventProcessed event = getEvent(1, "Talk", Collections.emptyList(), true, "1000+ edits");
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsNotLoggedIn() {
        EventProcessed event = getEvent(1, "Talk", groups, false, "1000+ edits");
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsWrongUserEditCountBucket() {
        EventProcessed event = getEvent(1, "Talk", groups, true, "5-99 edits");
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventPassesPerformerRegistrationDtDeserializes() {
        EventProcessed event = getEvent();
        event.setPerformerData(
                PerformerData.builder()
                        .groups(groups)
                        .isLoggedIn(true).editCountBucket("1000+ edits")
                        .registrationDt(Instant.parse("2023-03-01T01:08:30Z"))
                        .build()
        );
        assertThat(curationFilter.apply(event)).isTrue();
    }

    @Test void testEventPassesCurationFilters() {
        EventProcessed event = getEvent(1, "Talk", groups, true, "1000+ edits");
        assertThat(curationFilter.apply(event)).isTrue();
    }

    @Test void testEventFailsEqualsRule() {
        EventProcessed event = getEvent(1, "Main", groups, true, "1000+ edits");
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsCollectionContainsAnyRule() {
        EventProcessed event = getEvent(1, "Talk", Collections.singletonList("*"), true, "1000+ edits");
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsCollectionDoesNotContainRule() {
        EventProcessed event = getEvent(1, "Talk", Arrays.asList("foo", "bar"), true, "1000+ edits");
        assertThat(curationFilter.apply(event)).isFalse();
    }
}
