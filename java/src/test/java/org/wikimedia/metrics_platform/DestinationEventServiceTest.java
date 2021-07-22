package org.wikimedia.metrics_platform;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DestinationEventServiceTest {

    @Test
    public void testDestinationEventService() {
        DestinationEventService loggingService = DestinationEventService.LOGGING;
        assertThat(loggingService.getBaseUri(), is("https://intake-logging.wikimedia.org"));
    }

}
