package org.wikimedia.metrics_platform;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DestinationEventServiceTest {

    @Test
    public void testDestinationEventService() {
        DestinationEventService loggingService = DestinationEventService.ERROR_LOGGING;
        assertThat(loggingService.getBaseUri()).isEqualTo("https://intake-logging.wikimedia.org");
    }

    @Test
    public void testDestinationEventServiceLocal() {
        DestinationEventService loggingService = DestinationEventService.LOCAL;
        assertThat(loggingService.getBaseUri()).isEqualTo("http://localhost:8192/v1/events");
    }

}
