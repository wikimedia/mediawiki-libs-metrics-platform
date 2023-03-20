package org.wikimedia.metrics_platform;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.wikimedia.metrics_platform.ConsistencyITClientMetadata.createConsistencyTestClientMetadata;
import static org.wikimedia.metrics_platform.config.StreamConfigFetcher.ANALYTICS_API_ENDPOINT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.config.SourceConfig;
import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.config.StreamConfigFetcher;
import org.wikimedia.metrics_platform.event.Event;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class ConsistencyIT {

    @Test void testConsistency() throws IOException {
        Path pathStreamConfigs = Paths.get("../tests/consistency/stream_configs.json");
        Path pathExpectedEvent = Paths.get("../tests/consistency/expected_event.json");

        try (BufferedReader reader = Files.newBufferedReader(pathStreamConfigs)) {

            // Init shared test config + variables for creating new metrics client + event processor.
            Map<String, StreamConfig> testStreamConfigs = getTestStreamConfigs(reader);
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

            consistencyTestMetricsClient.submitMetricsEvent(
                    "test_consistency_event",
                    singletonMap("test", "consistency")
            );

            Event queuedEvent = eventQueue.peek();
            consistencyTestEventProcessor.eventPassesCurationRules(queuedEvent, testStreamConfigs);

            // Adjust the queuedEvent and compare it against the expected event.
            try (BufferedReader expectedEventReader = Files.newBufferedReader(pathExpectedEvent)) {
                JsonObject expectedEventJsonObject = JsonParser.parseReader(expectedEventReader).getAsJsonObject();

                Gson gson = GsonHelper.getGson();
                String queuedEventJsonStringRaw = gson.toJson(queuedEvent);
                JsonObject queuedEventJsonObject = JsonParser.parseString(queuedEventJsonStringRaw).getAsJsonObject();
                // Remove the timestamp properties from the queued event to match the expected event json.
                removeExtraProperties(queuedEventJsonObject);

                assertThat(queuedEventJsonObject).isEqualTo(expectedEventJsonObject);
            }
        }
    }

    private static MetricsClient getTestMetricsClient(
            ClientMetadata consistencyTestClientMetadata,
            AtomicReference<SourceConfig> sourceConfigRef,
            BlockingQueue<Event> eventQueue
    ) {
        return MetricsClient.builder(consistencyTestClientMetadata)
                .sourceConfigRef(sourceConfigRef)
                .eventQueue(eventQueue)
                .build();
    }

    private static EventProcessor getTestEventProcessor(
            ClientMetadata consistencyTestClientMetadata,
            AtomicReference<SourceConfig> sourceConfigRef,
            BlockingQueue<Event> eventQueue
    ) {
        ContextController contextController = new ContextController(consistencyTestClientMetadata);
        EventSender eventSender = new TestEventSender();
        return new EventProcessor(
                contextController,
                sourceConfigRef,
                eventSender,
                eventQueue
        );
    }

    private static Map<String, StreamConfig> getTestStreamConfigs(Reader reader) throws MalformedURLException {
        StreamConfigFetcher streamConfigFetcher = new StreamConfigFetcher(new URL(ANALYTICS_API_ENDPOINT));
        return streamConfigFetcher.parseConfig(reader);

    }

    private static void removeExtraProperties(JsonObject eventJsonObject) {
        eventJsonObject.remove("dt");
        eventJsonObject.getAsJsonObject("performer").remove("registration_dt");
    }
}
