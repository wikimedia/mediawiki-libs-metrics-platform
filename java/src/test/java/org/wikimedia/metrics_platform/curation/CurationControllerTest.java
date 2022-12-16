package org.wikimedia.metrics_platform.curation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.Event;
import org.wikimedia.metrics_platform.config.SourceConfigFixtures;
import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.context.UserData;

class CurationControllerTest {

    private final StreamConfig streamConfig = SourceConfigFixtures.STREAM_CONFIGS_WITH_EVENTS.get("test.stream");
    private final CurationController curationController = new CurationController();

    private Event event;

    @BeforeEach void resetEvent() {
        event = new Event("test/event", "test.stream", "test.event");
    }

    @Test void testEventPassesCurationFilters() {
        UserData userData = new UserData();
        userData.setGroups(Collections.singleton("steward"));
        event.getPageData().setTitle("Test");
        event.setUserData(userData);
        assertThat(curationController.eventPassesCurationRules(event, streamConfig)).isTrue();
    }

    @Test void testEventFailsEqualsRule() {
        event.getPageData().setTitle("Not Test");
        UserData userData = new UserData();
        userData.setGroups(Collections.singleton("steward"));
        event.setUserData(userData);
        assertThat(curationController.eventPassesCurationRules(event, streamConfig)).isFalse();
    }

    @Test void testEventFailsCollectionContainsAnyRule() {
        UserData userData = new UserData();
        userData.setGroups(Collections.singleton("*"));
        event.getPageData().setTitle("Test");
        event.setUserData(userData);
        assertThat(curationController.eventPassesCurationRules(event, streamConfig)).isFalse();
    }

    @Test void testEventFailsCollectionDoesNotContainRule() {
        UserData userData = new UserData();
        userData.setGroups(new HashSet<>(Arrays.asList("steward", "sysop")));
        event.getPageData().setTitle("Test");
        event.setUserData(userData);
        assertThat(curationController.eventPassesCurationRules(event, streamConfig)).isFalse();
    }

}
