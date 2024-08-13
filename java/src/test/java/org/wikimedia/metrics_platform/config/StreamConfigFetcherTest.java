package org.wikimedia.metrics_platform.config;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.wikimedia.metrics_platform.config.DestinationEventService.LOCAL;
import static org.wikimedia.metrics_platform.config.SourceConfigFixtures.getTestSourceConfigMax;
import static org.wikimedia.metrics_platform.config.StreamConfigFetcher.ANALYTICS_API_ENDPOINT;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.io.Resources;

import okhttp3.OkHttpClient;

class StreamConfigFetcherTest {

    @Test void parsingConfigFromJsonWorks() throws IOException {
        try (Reader in = readConfigFile("streamconfigs.json")) {
            StreamConfigFetcher streamConfigFetcher = new StreamConfigFetcher(new URL(ANALYTICS_API_ENDPOINT), new OkHttpClient());
            Map<String, StreamConfig> config = streamConfigFetcher.parseConfig(in);
            assertThat(config).containsKey("mediawiki.visual_editor_feature_use");
            StreamConfig streamConfig = config.get("mediawiki.visual_editor_feature_use");
            String schemaTitle = streamConfig.getSchemaTitle();
            assertThat(schemaTitle).isEqualTo("analytics/mediawiki/client/metrics_event");
            assertThat(streamConfig.getStreamName()).isEqualTo("mediawiki.visual_editor_feature_use");
        }
    }

    @Test void parsingLocalConfigFromJsonWorks() throws IOException {
        try (Reader in = readConfigFile("streamconfigs-local.json")) {
            StreamConfigFetcher streamConfigFetcher = new StreamConfigFetcher(new URL(ANALYTICS_API_ENDPOINT), new OkHttpClient());
            Map<String, StreamConfig> config = streamConfigFetcher.parseConfig(in);
            assertThat(config).containsKey("mediawiki.visual_editor_feature_use");
            StreamConfig streamConfig = config.get("mediawiki.edit_attempt");
            assertThat(streamConfig.getDestinationEventService()).isEqualTo(LOCAL);
        }
    }

    @Test void testCacheHit() throws IOException {
        StreamConfigFetcher streamConfigFetcher = new StreamConfigFetcher(new URL(ANALYTICS_API_ENDPOINT), new OkHttpClient());
        SourceConfig sourceConfig = getTestSourceConfigMax();
        streamConfigFetcher.streamConfigCache.put("metrics_platform_stream_configs", sourceConfig);
        SourceConfig cachedSourceConfig = streamConfigFetcher.streamConfigCache.getIfPresent("metrics_platform_stream_configs");
        StreamConfig streamConfig = cachedSourceConfig.getStreamConfigByName("test_stream");

        assertThat(streamConfig).isNotNull();
        assertThat(streamConfig.hasEvents()).isTrue();
        assertThat(streamConfig.hasSampleConfig()).isTrue();
    }

    @Test void testCacheMiss() throws IOException {
        StreamConfigFetcher streamConfigFetcher = new StreamConfigFetcher(new URL(ANALYTICS_API_ENDPOINT), new OkHttpClient());
        SourceConfig cachedSourceConfig = streamConfigFetcher.streamConfigCache.getIfPresent("metrics_platform_stream_configs");
        assertThat(cachedSourceConfig).isNull();
    }

    @Test void testPassingInSourceConfigs() throws IOException {
        SourceConfig sourceConfig = getTestSourceConfigMax();
        StreamConfigFetcher streamConfigFetcher = new StreamConfigFetcher(new URL(ANALYTICS_API_ENDPOINT), new OkHttpClient(), sourceConfig);
        SourceConfig sourceConfigParameter = streamConfigFetcher.getSourceConfig();
        StreamConfig streamConfig = sourceConfigParameter.getStreamConfigByName("test_stream");

        assertThat(streamConfig).isNotNull();
        assertThat(streamConfig.hasEvents()).isTrue();
        assertThat(streamConfig.hasSampleConfig()).isTrue();
    }

    private Reader readConfigFile(String filename) throws IOException {
        return Resources.asCharSource(
                Resources.getResource("org/wikimedia/metrics_platform/config/" + filename),
                UTF_8
        ).openStream();
    }
}
