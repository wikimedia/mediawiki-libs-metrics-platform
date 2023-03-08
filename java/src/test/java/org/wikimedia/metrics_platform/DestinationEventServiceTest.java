package org.wikimedia.metrics_platform;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.config.DestinationEventService;

class DestinationEventServiceTest {

    @Test void testDestinationEventService() throws MalformedURLException {
        DestinationEventService loggingService = DestinationEventService.ERROR_LOGGING;
        assertThat(loggingService.getBaseUri()).isEqualTo(new URL("https://intake-logging.wikimedia.org"));
    }

    @Test void testDestinationEventServiceLocal() throws MalformedURLException {
        DestinationEventService loggingService = DestinationEventService.LOCAL;
        assertThat(loggingService.getBaseUri()).isEqualTo(new URL("http://localhost:8192/v1/events"));
    }

}
