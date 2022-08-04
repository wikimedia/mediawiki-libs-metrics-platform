package org.wikimedia.metrics_platform;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

public class SessionControllerTest {

    @Test
    public void testBeginNewSession() {
        SessionController sessionController = new SessionController();
        String oldSessionId = sessionController.getSessionId();
        sessionController.beginNewSession();
        assertThat(sessionController.getSessionId(), not(oldSessionId));
    }

    @Test
    public void testSessionExpiry() {
        Instant oneHourAgo = Instant.now().minus(1, HOURS);
        SessionController sessionController = new SessionController(oneHourAgo);
        assertThat(sessionController.sessionExpired(), is(true));
    }

    @Test
    public void testTouchSession() {
        Instant oneHourAgo = Instant.now().minus(1, HOURS);
        SessionController sessionController = new SessionController(oneHourAgo);
        sessionController.touchSession();
        assertThat(sessionController.sessionExpired(), is(false));
    }

}
