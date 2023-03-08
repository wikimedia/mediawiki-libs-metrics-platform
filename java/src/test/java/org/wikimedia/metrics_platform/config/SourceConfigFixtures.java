package org.wikimedia.metrics_platform.config;

import static org.wikimedia.metrics_platform.config.StreamConfigFixtures.streamConfigMap;

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

    public static SourceConfig getTestSourceConfig(CurationFilter curationFilter) {
        return new SourceConfig(streamConfigMap(curationFilter));
    }
}
