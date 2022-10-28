package org.wikimedia.metrics_platform.curation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.Event;
import org.wikimedia.metrics_platform.StreamConfig;
import org.wikimedia.metrics_platform.TestStreamConfigsFetcher;
import org.wikimedia.metrics_platform.context.UserData;

public class CurationControllerTest {

    private final StreamConfig streamConfig = TestStreamConfigsFetcher.STREAM_CONFIGS.get("test.event");
    private final CurationController curationController = new CurationController();

    private Event event;

    @BeforeEach
    public void resetEvent() {
        event = new Event("test/event", "test.event", "testEvent");
    }

    @Test
    public void testEventPassesCurationFilters() {
        UserData userData = new UserData();
        userData.setGroups(Collections.singleton("steward"));
        event.getPageData().setTitle("Test");
        event.setUserData(userData);
        assertThat(curationController.eventPassesCurationRules(event, streamConfig), is(true));
    }

    @Test
    public void testEventFailsEqualsRule() {
        event.getPageData().setTitle("Not Test");
        UserData userData = new UserData();
        userData.setGroups(Collections.singleton("steward"));
        event.setUserData(userData);
        assertThat(curationController.eventPassesCurationRules(event, streamConfig), is(false));
    }

    @Test
    public void testEventFailsCollectionContainsAnyRule() {
        UserData userData = new UserData();
        userData.setGroups(Collections.singleton("*"));
        event.getPageData().setTitle("Test");
        event.setUserData(userData);
        assertThat(curationController.eventPassesCurationRules(event, streamConfig), is(false));
    }

    @Test
    public void testEventFailsCollectionDoesNotContainRule() {
        UserData userData = new UserData();
        userData.setGroups(new HashSet<>(Arrays.asList("steward", "sysop")));
        event.getPageData().setTitle("Test");
        event.setUserData(userData);
        assertThat(curationController.eventPassesCurationRules(event, streamConfig), is(false));
    }

}
