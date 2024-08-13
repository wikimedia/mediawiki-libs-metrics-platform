package org.wikimedia.metrics_platform.config;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.wikimedia.metrics_platform.json.GsonHelper;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class StreamConfigFetcher {
    public static final String ANALYTICS_API_ENDPOINT = "https://meta.wikimedia.org/w/api.php?" +
            "action=streamconfigs&format=json&formatversion=2&" +
            "constraints=destination_event_service%3Deventgate-analytics-external";

    public static final String METRICS_PLATFORM_SCHEMA_TITLE = "analytics/mediawiki/client/metrics_event";

    private final URL url;
    private final OkHttpClient httpClient;
    @Getter
    private SourceConfig sourceConfig;

    public StreamConfigFetcher(URL url, OkHttpClient httpClient) {
        this.url = url;
        this.httpClient = httpClient;
        this.sourceConfig = streamConfigCache.getIfPresent("metrics_platform_stream_configs");
    }

    public StreamConfigFetcher(URL url, OkHttpClient httpClient, SourceConfig sourceConfig) {
        this.url = url;
        this.httpClient = httpClient;
        streamConfigCache.put("metrics_platform_stream_configs", sourceConfig);
        this.sourceConfig = sourceConfig;
    }

    /**
     * Initialization of the StreamConfigFetcher cache.
     */
    Cache<String, SourceConfig> streamConfigCache = Caffeine.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    /**
     * Fetch stream configs from analytics endpoint.
     */
    public SourceConfig fetchStreamConfigs() throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = httpClient.newCall(request).execute();
        ResponseBody body = response.body();
        if (body == null) {
            throw new IOException("Failed to fetch stream configs: " + response.message());
        }
        sourceConfig = new SourceConfig(parseConfig(body.charStream()));
        streamConfigCache.put("metrics_platform_stream_configs", sourceConfig);
        return sourceConfig;
    }

    // Visible For Testing
    public Map<String, StreamConfig> parseConfig(Reader reader) {
        return GsonHelper.getGson().fromJson(reader, StreamConfigCollection.class).streamConfigs.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
