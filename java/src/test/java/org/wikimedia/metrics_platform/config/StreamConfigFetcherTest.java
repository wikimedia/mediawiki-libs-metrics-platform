package org.wikimedia.metrics_platform.config;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.wikimedia.metrics_platform.config.StreamConfigFetcher.ANALYTICS_API_ENDPOINT;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import org.junit.jupiter.api.Test;

class StreamConfigFetcherTest {

    @Test void ParsingConfigFromJsonWorks() throws IOException {
        try (InputStreamReader in = readConfigFile()) {
            StreamConfigFetcher streamConfigFetcher = new StreamConfigFetcher(new URL(ANALYTICS_API_ENDPOINT));
            Map<String, StreamConfig> config = streamConfigFetcher.parseConfig(in);
            assertThat(config).containsKey("analytics/mediawiki/client/metrics_event");

            StreamConfig streamConfig = config.get("analytics/mediawiki/client/metrics_event");
            assertThat(streamConfig.getStreamName()).isEqualTo("mediawiki.visual_editor_feature_use");
        }
    }

    private InputStreamReader readConfigFile() {
        return new InputStreamReader(
                StreamConfigFetcher.class.getClassLoader()
                        .getResourceAsStream("org/wikimedia/metrics_platform/config/streamconfigs.json"),
                UTF_8
        );
    }
}
