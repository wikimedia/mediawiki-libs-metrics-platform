package org.wikimedia.metrics_platform;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

public class SessionControllerTest {

    @Test
    public void testSessionExpiry() {
        Instant oneHourAgo = Instant.now().minus(1, HOURS);
        SessionController sessionController = new SessionController(oneHourAgo);
        assertThat(sessionController.sessionExpired()).isEqualTo(true);
    }

    @Test
    public void testTouchSession() {
        Instant oneHourAgo = Instant.now().minus(1, HOURS);
        SessionController sessionController = new SessionController(oneHourAgo);
        String sessionId1 = sessionController.getSessionId();
        sessionController.touchSession();
        assertThat(sessionController.sessionExpired()).isEqualTo(false);

        String sessionId2 = sessionController.getSessionId();

        assertThat(sessionId1).isNotEqualTo(sessionId2);
    }
}
