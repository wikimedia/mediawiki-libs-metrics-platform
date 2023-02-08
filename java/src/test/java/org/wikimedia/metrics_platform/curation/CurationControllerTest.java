package org.wikimedia.metrics_platform.curation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.wikimedia.metrics_platform.config.CurationFilterFixtures.getCurationFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.CurationController;
import org.wikimedia.metrics_platform.config.CurationFilter;
import org.wikimedia.metrics_platform.context.PerformerData;
import org.wikimedia.metrics_platform.event.Event;

class CurationControllerTest {

    private final CurationController curationController = new CurationController();

    private Event event;

    @BeforeEach void resetEvent() {
        event = new Event("test/event", "test.stream", "test.event");
    }

    @Test void testEventPassesCurationFilters() {
        PerformerData performerData = new PerformerData();
        performerData.setGroups(Collections.singleton("steward"));
        event.getPageData().setTitle("Test");
        event.setPerformerData(performerData);
        CurationFilter curationFilter = getCurationFilter();
        assertThat(curationController.eventPassesCurationRules(event, curationFilter)).isTrue();
    }

    @Test void testEventFailsEqualsRule() {
        event.getPageData().setTitle("Not Test");
        PerformerData performerData = new PerformerData();
        performerData.setGroups(Collections.singleton("steward"));
        event.setPerformerData(performerData);
        CurationFilter curationFilter = getCurationFilter();
        assertThat(curationController.eventPassesCurationRules(event, curationFilter)).isFalse();
    }

    @Test void testEventFailsCollectionContainsAnyRule() {
        PerformerData performerData = new PerformerData();
        performerData.setGroups(Collections.singleton("*"));
        event.getPageData().setTitle("Test");
        event.setPerformerData(performerData);
        CurationFilter curationFilter = getCurationFilter();
        assertThat(curationController.eventPassesCurationRules(event, curationFilter)).isFalse();
    }

    @Test void testEventFailsCollectionDoesNotContainRule() {
        PerformerData performerData = new PerformerData();
        performerData.setGroups(new HashSet<>(Arrays.asList("steward", "sysop")));
        event.getPageData().setTitle("Test");
        event.setPerformerData(performerData);
        CurationFilter curationFilter = getCurationFilter();
        assertThat(curationController.eventPassesCurationRules(event, curationFilter)).isFalse();
    }

}
