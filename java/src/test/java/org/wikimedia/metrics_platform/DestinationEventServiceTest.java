package org.wikimedia.metrics_platform;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class DestinationEventServiceTest {

    @Test
    public void testDestinationEventService() {
        DestinationEventService loggingService = DestinationEventService.ERROR_LOGGING;
        assertThat(loggingService.getBaseUri(), is("https://intake-logging.wikimedia.org"));
    }

}
