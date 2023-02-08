package org.wikimedia.metrics_platform.curation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.event.Event;
import org.wikimedia.metrics_platform.config.CurationFilter;
import org.wikimedia.metrics_platform.context.PageData;
import org.wikimedia.metrics_platform.context.PerformerData;

import com.google.gson.Gson;

class CurationFilterTest {

    private static CurationFilter curationFilter;

    @BeforeAll static void setUp() {
        Gson gson = new Gson();
        String curationFilterJson = "{\"page_id\":{\"less_than\":500,\"not_equals\":42},\"page_namespace_name\":" +
                "{\"equals\":\"Talk\"},\"performer_is_logged_in\":{\"equals\":true},\"performer_edit_count_bucket\":" +
                "{\"in\":[\"100-999 edits\",\"1000+ edits\"]},\"performer_groups\":{\"contains_all\":" +
                "[\"user\",\"autoconfirmed\"],\"does_not_contain\":\"sysop\"}}";
        curationFilter = gson.fromJson(curationFilterJson, CurationFilter.class);
    }

    private static Event getBaseEvent() {
        PageData pageData = PageData.builder().id(1).namespaceName("Talk").build();
        PerformerData performerData = PerformerData.builder().groups(Arrays.asList("user", "autoconfirmed", "steward"))
                .isLoggedIn(true).editCountBucket("1000+ edits").build();

        Event event = new Event("test/event", "test.event", "testEvent");
        event.setPageData(pageData);
        event.setPerformerData(performerData);
        return event;
    }

    @Test void testEventPasses() {
        assertThat(curationFilter.apply(getBaseEvent())).isTrue();
    }

    @Test void testEventFailsWrongPageId() {
        Event event = getBaseEvent();
        event.getPageData().setId(42);
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsWrongPageNamespaceText() {
        Event event = getBaseEvent();
        event.getPageData().setNamespaceName("User");
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsWrongUserGroups() {
        Event event = getBaseEvent();
        event.getPerformerData().setGroups(Arrays.asList("user", "autoconfirmed", "sysop"));
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsNoUserGroups() {
        Event event = getBaseEvent();
        event.getPerformerData().setGroups(Collections.emptyList());
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsNotLoggedIn() {
        Event event = getBaseEvent();
        event.getPerformerData().setIsLoggedIn(false);
        assertThat(curationFilter.apply(event)).isFalse();
    }

    @Test void testEventFailsWrongUserEditCountBucket() {
        Event event = getBaseEvent();
        event.getPerformerData().setEditCountBucket("5-99 edits");
        assertThat(curationFilter.apply(event)).isFalse();
    }
}
