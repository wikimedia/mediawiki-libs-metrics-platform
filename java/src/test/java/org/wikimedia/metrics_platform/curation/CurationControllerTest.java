package org.wikimedia.metrics_platform.curation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.Event;
import org.wikimedia.metrics_platform.StreamConfig;
import org.wikimedia.metrics_platform.TestMetricsClientIntegration;
import org.wikimedia.metrics_platform.context.PageData;
import org.wikimedia.metrics_platform.context.UserData;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CurationControllerTest {

    private final StreamConfig streamConfig = TestMetricsClientIntegration.STREAM_CONFIGS.get("test.event");
    private final CurationController curationController = new CurationController();

    private Event event;

    @BeforeEach
    public void resetEvent() {
        event = new Event("test/event", "test.event");
    }

    @Test
    public void testEventPassesCurationFilters() {
        PageData pageData = new PageData();
        pageData.setTitle("Test");
        UserData userData = new UserData();
        userData.setGroups(Collections.singleton("steward"));
        event.setPageData(pageData);
        event.setUserData(userData);
        assertThat(curationController.eventPassesCurationRules(event, streamConfig), is(true));
    }

    @Test
    public void testEventFailsEqualsRule() {
        PageData pageData = new PageData();
        pageData.setTitle("Not Test");
        UserData userData = new UserData();
        userData.setGroups(Collections.singleton("steward"));
        event.setPageData(pageData);
        event.setUserData(userData);
        assertThat(curationController.eventPassesCurationRules(event, streamConfig), is(false));
    }

    @Test
    public void testEventFailsCollectionContainsAnyRule() {
        PageData pageData = new PageData();
        pageData.setTitle("Test");
        UserData userData = new UserData();
        userData.setGroups(Collections.singleton("*"));
        event.setPageData(pageData);
        event.setUserData(userData);
        assertThat(curationController.eventPassesCurationRules(event, streamConfig), is(false));
    }

    @Test
    public void testEventFailsCollectionDoesNotContainRule() {
        PageData pageData = new PageData();
        pageData.setTitle("Test");
        UserData userData = new UserData();
        userData.setGroups(new HashSet<>(Arrays.asList("steward", "sysop")));
        event.setPageData(pageData);
        event.setUserData(userData);
        assertThat(curationController.eventPassesCurationRules(event, streamConfig), is(false));
    }

}
