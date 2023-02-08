package org.wikimedia.metrics_platform.config;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.wikimedia.metrics_platform.DestinationEventService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class StreamConfigFetcher {
    public static final String ANALYTICS_API_ENDPOINT = "https://meta.wikimedia.org/w/api.php?" +
            "action=streamconfigs&format=json&formatversion=2&all_settings=1&" +
            "constraints=destination_event_service%3Deventgate-analytics-external";

    public static final String METRICS_PLATFORM_SCHEMA_TITLE = "analytics/mediawiki/client/metrics_event";

    private final Gson gson = new GsonBuilder().registerTypeAdapter(
            StreamConfig.class, new StreamConfigJsonDeserializer()
    ).serializeNulls().create();
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
        Map<String, StreamConfig> streamConfigs = new HashMap<>();
        JsonElement rootObject = JsonParser.parseReader(new JsonReader(reader));
        JsonObject streamsRoot = rootObject.getAsJsonObject();
        JsonObject streams = streamsRoot.get("streams").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : streams.entrySet()) {
            JsonObject currentStream = entry.getValue().getAsJsonObject();
            String streamName = currentStream.get("stream").getAsString();
            String schemaTitle = currentStream.get("schema_title").getAsString();
            if (METRICS_PLATFORM_SCHEMA_TITLE.equals(schemaTitle)) {
                StreamConfig streamConfig = gson.fromJson(currentStream, StreamConfig.class);
                streamConfigs.put(streamName, streamConfig);
            }
        }
        return streamConfigs;
    }

    /**
     * Custom deserializer for Metrics Platform stream configs.
     */
    public static class StreamConfigJsonDeserializer implements JsonDeserializer<StreamConfig> {

        public StreamConfig deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context
        ) throws JsonParseException {

            JsonObject streamConfigJson = json.getAsJsonObject();
            String streamName = streamConfigJson.get("stream").getAsString();
            String schemaTitle = streamConfigJson.get("schema_title").getAsString();
            JsonObject producers = streamConfigJson.get("producers").getAsJsonObject();
            JsonObject metricsPlatformClient = producers.get("metrics_platform_client").getAsJsonObject();

            JsonElement eventsJson = metricsPlatformClient.get("events");
            Set<String> events = convertToSet(eventsJson);

            JsonElement provideValuesJson = metricsPlatformClient.get("provide_values");
            Set<String> provideValues = convertToSet(provideValuesJson);

            JsonObject sample = streamConfigJson.get("sample").getAsJsonObject();
            SampleConfig.Identifier unit = SampleConfig.Identifier.valueOf(sample.get("unit").getAsString().toUpperCase(Locale.ROOT));
            double rate = sample.get("rate").getAsDouble();
            SampleConfig sampleConfig = new SampleConfig(rate, unit);
            StreamConfig.MetricsPlatformClientConfig mpcConfig = new StreamConfig.MetricsPlatformClientConfig(
                events,
                provideValues,
                null
            );
            StreamConfig.ProducerConfig producerConfig = new StreamConfig.ProducerConfig(mpcConfig);

            return new StreamConfig(
                streamName,
                schemaTitle,
                DestinationEventService.ANALYTICS,
                producerConfig,
                sampleConfig
            );
        }

        private static Set<String> convertToSet(JsonElement element) {
            return StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
                    .map(JsonElement::getAsString)
                    .collect(toSet());
        }
    }
}
