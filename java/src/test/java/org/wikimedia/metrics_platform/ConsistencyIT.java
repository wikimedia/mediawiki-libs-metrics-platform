package org.wikimedia.metrics_platform;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.wikimedia.metrics_platform.ConsistencyITClientMetadata.createConsistencyTestClientMetadata;
import static org.wikimedia.metrics_platform.config.StreamConfigFetcher.ANALYTICS_API_ENDPOINT;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.config.SourceConfig;
import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.config.StreamConfigFetcher;
import org.wikimedia.metrics_platform.context.ContextController;
import org.wikimedia.metrics_platform.curation.CurationController;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SuppressWarnings("checkstyle:classfanoutcomplexity")
class ConsistencyIT {

    @Test void testConsistency() throws IOException {
        Path pathStreamConfigs = Paths.get("../tests/consistency/stream_configs.json");
        Path pathExpectedEvent = Paths.get("../tests/consistency/expected_event.json");

        try (BufferedReader reader = Files.newBufferedReader(pathStreamConfigs)) {
            InputStreamReader targetStreamReader = convertToInputStreamReader(reader);

            // Init shared test config + variables for creating new metrics client + event processor.
            Map<String, StreamConfig> testStreamConfigs = getTestStreamConfigs(targetStreamReader);
            SourceConfig sourceConfig = new SourceConfig(testStreamConfigs);
            AtomicReference<SourceConfig> sourceConfigRef = new AtomicReference<>();
            sourceConfigRef.set(sourceConfig);
            BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>(10);
            ClientMetadata consistencyTestClientMetadata = createConsistencyTestClientMetadata();

            EventProcessor consistencyTestEventProcessor = getTestEventProcessor(
                    consistencyTestClientMetadata,
                    sourceConfigRef,
                    eventQueue
            );

            MetricsClient consistencyTestMetricsClient = getTestMetricsClient(
                    consistencyTestClientMetadata,
                    sourceConfigRef,
                    eventQueue
            );

            consistencyTestMetricsClient.dispatch(
                    "test_consistency_event",
                    singletonMap("test", "consistency")
            );

            Event queuedEvent = eventQueue.peek();
            consistencyTestEventProcessor.eventPassesCurationRules(queuedEvent, testStreamConfigs);

            // Adjust the queuedEvent and compare it against the expected event.
            try (BufferedReader expectedEventReader = Files.newBufferedReader(pathExpectedEvent)) {
                JsonObject expectedEventJsonObject = JsonParser.parseReader(expectedEventReader).getAsJsonObject();

                Gson gson = new Gson();
                String expectedEventJsonString = gson.toJson(expectedEventJsonObject);
                String queuedEventJsonStringRaw = gson.toJson(queuedEvent);
                JsonObject queuedEventJsonObject = JsonParser.parseString(queuedEventJsonStringRaw).getAsJsonObject();

                // For now we are removing properties that output empty values in order to match the expected
                // event json. This will be addressed by https://phabricator.wikimedia.org/T328458 upon
                // completion of which, the following line and method can be removed.
                removeExtraProperties(queuedEventJsonObject);
                String queuedEventJsonString = gson.toJson(queuedEventJsonObject);

                JsonElement expectedEventJsonElement = JsonParser.parseString(expectedEventJsonString);
                JsonElement queuedEventJsonElement = JsonParser.parseString(queuedEventJsonString);

                assertEquals(expectedEventJsonElement, queuedEventJsonElement);
            }
        }
    }

    private static MetricsClient getTestMetricsClient(
            ClientMetadata consistencyTestClientMetadata,
            AtomicReference<SourceConfig> sourceConfigRef,
            BlockingQueue<Event> eventQueue
    ) {
        SessionController sessionController = new SessionController();
        SamplingController samplingController = new SamplingController(consistencyTestClientMetadata, sessionController);

        return new MetricsClient(
                consistencyTestClientMetadata,
                sessionController,
                samplingController,
                sourceConfigRef,
                eventQueue
        );
    }

    private static EventProcessor getTestEventProcessor(
            ClientMetadata consistencyTestClientMetadata,
            AtomicReference<SourceConfig> sourceConfigRef,
            BlockingQueue<Event> eventQueue
    ) {
        ContextController contextController = new ContextController(consistencyTestClientMetadata);
        CurationController curationController = new CurationController();
        EventSender eventSender = new EventSender() {
            @Override
            public void sendEvents(String baseUri, Collection<Event> events) throws IOException {
            }
        };
        return new EventProcessor(
                contextController,
                curationController,
                sourceConfigRef,
                eventSender,
                eventQueue
        );
    }

    private static Map<String, StreamConfig> getTestStreamConfigs(InputStreamReader reader) throws MalformedURLException {
        StreamConfigFetcher streamConfigFetcher = new StreamConfigFetcher(new URL(ANALYTICS_API_ENDPOINT));
        return streamConfigFetcher.parseConfig(reader);

    }

    private static InputStreamReader convertToInputStreamReader(BufferedReader reader) throws IOException {
        char[] charBuffer = new char[8 * 1024];
        StringBuilder builder = new StringBuilder();
        int numCharsRead;
        while ((numCharsRead = reader.read(charBuffer, 0, charBuffer.length)) != -1) {
            builder.append(charBuffer, 0, numCharsRead);
        }
        InputStream targetStream = new ByteArrayInputStream(builder.toString().getBytes(StandardCharsets.UTF_8));
        return new InputStreamReader(targetStream, StandardCharsets.UTF_8);
    }

    private static void removeExtraProperties(JsonObject eventJsonObject) {
        eventJsonObject.remove("dt");
        eventJsonObject
                .getAsJsonObject("mediawiki")
                .remove("site_content_language_variant");
        eventJsonObject.getAsJsonObject("performer").remove("name");
        eventJsonObject.getAsJsonObject("performer").remove("language_variant");
        eventJsonObject.getAsJsonObject("performer").remove("edit_count");
        eventJsonObject.getAsJsonObject("performer").remove("edit_count_bucket");
        eventJsonObject.getAsJsonObject("performer").remove("registration_dt");
    }
}
