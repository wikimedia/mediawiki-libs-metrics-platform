package org.wikimedia.metrics_platform;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

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
        Date oneHourAgo = new Date((new Date()).getTime() - 3600000);
        SessionController sessionController = new SessionController(oneHourAgo);
        assertThat(sessionController.sessionExpired(), is(true));
    }

    @Test
    public void testTouchSession() {
        Date oneHourAgo = new Date((new Date()).getTime() - 3600000);
        SessionController sessionController = new SessionController(oneHourAgo);
        sessionController.touchSession();
        assertThat(sessionController.sessionExpired(), is(false));
    }

}
