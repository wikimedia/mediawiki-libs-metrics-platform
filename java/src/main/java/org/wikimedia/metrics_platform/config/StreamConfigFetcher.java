package org.wikimedia.metrics_platform.config;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

import org.wikimedia.metrics_platform.json.GsonHelper;

public class StreamConfigFetcher {
    public static final String ANALYTICS_API_ENDPOINT = "https://meta.wikimedia.org/w/api.php?" +
            "action=streamconfigs&format=json&formatversion=2&" +
            "constraints=destination_event_service%3Deventgate-analytics-external";

    public static final String METRICS_PLATFORM_SCHEMA_TITLE = "analytics/mediawiki/client/metrics_event";

    private final URL url;

    public StreamConfigFetcher(URL url) {
        this.url = url;
    }

    /**
     * Fetch stream configs from analytics endpoint.
     */
    public SourceConfig fetchStreamConfigs() throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(url.openStream(), UTF_8)) {
            return new SourceConfig(parseConfig(inputStreamReader));
        }
    }

    // Visible For Testing
    public Map<String, StreamConfig> parseConfig(Reader reader) {
        return GsonHelper.getGson().fromJson(reader, StreamConfigCollection.class).streamConfigs.entrySet().stream()
            .filter(e -> e.getValue().getSchemaTitle().equals(METRICS_PLATFORM_SCHEMA_TITLE))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
