package org.wikimedia.metrics_platform;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Manages sessions and session IDs for the Metrics Platform Client.
 *
 * A session begins when the application is launched and expires when the app is in the background
 * for 15 minutes or more.
 */
@ThreadSafe
@ParametersAreNonnullByDefault
class SessionController {

    private static final Duration SESSION_TIMEOUT = Duration.of(15, MINUTES); // 15 minutes
    @GuardedBy("this")
    @Nonnull
    private String sessionId = generateSessionId();
    @GuardedBy("this")
    @Nonnull
    private Instant sessionTouched;

    SessionController() {
        this(Instant.now());
    }

    /**
     * Constructor for testing.
     *
     * @param date session start time
     */
    SessionController(Instant date) {
        this.sessionTouched = date;
    }

    @Nonnull
    synchronized String getSessionId() {
        return sessionId;
    }

    synchronized void touchSession() {
        sessionTouched = Instant.now();
    }

    synchronized boolean sessionExpired() {
        return Duration.between(sessionTouched, Instant.now()).compareTo(SESSION_TIMEOUT) >= 0;
    }

    @SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "TODO: This needs some non trivial refactoring")
    synchronized void beginNewSession() {
        sessionId = generateSessionId();
        touchSession();
    }

    @Nonnull
    private static String generateSessionId() {
        return UUID.randomUUID().toString();
    }

}
