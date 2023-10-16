package org.wikimedia.metrics_platform.config;

public final class SourceConfigFixtures {

    private SourceConfigFixtures() {
        // Utility class, should never be instantiated
    }

    /**
     * Convenience method for getting source config.
     */
    public static SourceConfig getTestSourceConfig() {
        return new SourceConfig(StreamConfigFixtures.streamConfigMap());
    }
}
