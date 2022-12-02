package org.wikimedia.metrics_platform.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SourceConfigTest {

    private static SourceConfig sourceConfig;

    @Test
    public void testConfig() {
        sourceConfig = new SourceConfig(SourceConfigFixtures.STREAM_CONFIGS_WITH_EVENTS);
        assertThat(sourceConfig.getStreamNamesByEvent("test.event")).containsExactly("test.stream");
    }

    @Test
    public void testGetStreamNames() {
        sourceConfig = new SourceConfig(SourceConfigFixtures.STREAM_CONFIGS_WITH_EVENTS);
        assertThat(sourceConfig.getStreamNames()).containsExactly("test.stream");
    }

    @Test
    public void testGetStreamConfigByName() {
        sourceConfig = new SourceConfig(SourceConfigFixtures.STREAM_CONFIGS_WITH_EVENTS);
        StreamConfig streamConfig = SourceConfigFixtures.getSampleStreamConfig(true);
        assertThat(streamConfig).isEqualTo(sourceConfig.getStreamConfigByName("test.stream"));
    }

    @Test
    public void testGetStreamNamesByEvent() {
        sourceConfig = new SourceConfig(SourceConfigFixtures.STREAM_CONFIGS_WITH_EVENTS);
        assertThat(sourceConfig.getStreamNamesByEvent("test.event")).containsExactly("test.stream");
    }
}
