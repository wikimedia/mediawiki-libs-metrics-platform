package org.wikimedia.metrics_platform.curation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.wikimedia.metrics_platform.event.EventFixtures.getEvent;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.GsonHelper;
import org.wikimedia.metrics_platform.config.CurationFilter;
import org.wikimedia.metrics_platform.event.EventProcessed;

import com.google.gson.Gson;

class CurationFilterTest {

    private static CurationFilter curationFilter;

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
        EventProcessed event = getEvent();
        event.getPageData().setId(42);
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsWrongPageNamespaceText() {
        EventProcessed event = getEvent();
        event.getPageData().setNamespaceName("User");
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsWrongUserGroups() {
        EventProcessed event = getEvent();
        event.getPerformerData().setGroups(Arrays.asList("user", "autoconfirmed", "sysop"));
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsNoUserGroups() {
        EventProcessed event = getEvent();
        event.getPerformerData().setGroups(Collections.emptyList());
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsNotLoggedIn() {
        EventProcessed event = getEvent();
        event.getPerformerData().setIsLoggedIn(false);
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsWrongUserEditCountBucket() {
        EventProcessed event = getEvent();
        event.getPerformerData().setEditCountBucket("5-99 edits");
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventPassesPerformerRegistrationDtDeserializes() {
        EventProcessed event = getEvent();
        event.getPerformerData().setRegistrationDt(Instant.parse("2023-03-01T01:08:30Z"));
        assertThat(curationFilter.apply(event)).isTrue();
    }
}
