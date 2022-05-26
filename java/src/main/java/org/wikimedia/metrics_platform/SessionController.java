package org.wikimedia.metrics_platform;

import java.util.Date;
import java.util.UUID;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Manages sessions and session IDs for the Metrics Platform client.
 *
 * A session begins when the application is launched and expires when the app is in the background
 * for 15 minutes or more.
 */
class SessionController {

    private static final int SESSION_TIMEOUT = 900000; // 15 minutes
    private static String SESSION_ID = generateSessionId();
    private Date sessionTouched;

    SessionController() {
        this(new Date());
    }

    /**
     * Constructor for testing.
     *
     * @param date session start time
     */
    SessionController(Date date) {
        this.sessionTouched = date;
    }

    String getSessionId() {
        return SESSION_ID;
    }

    void touchSession() {
        sessionTouched = new Date();
    }

    boolean sessionExpired() {
        return (new Date()).getTime() - sessionTouched.getTime() >= SESSION_TIMEOUT;
    }

    @SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "TODO: This needs some non trivial refactoring")
    void beginNewSession() {
        SESSION_ID = generateSessionId();
        touchSession();
    }

    private static String generateSessionId() {
        return UUID.randomUUID().toString();
    }

}
