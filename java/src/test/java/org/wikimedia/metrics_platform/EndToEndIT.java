package org.wikimedia.metrics_platform;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static java.util.Collections.singletonMap;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.wikimedia.metrics_platform.ConsistencyITClientMetadata.createConsistencyTestClientMetadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WireMockTest(httpPort = 8192)
public class EndToEndIT {
    @Test void submitEventTimerStreamConfig(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException {
        // Stub fetching the stream config from api endpoint.
        stubFor(get(urlEqualTo("/config"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(readConfig())));

        // Stub response from posting event to local eventgate logging service.
        stubFor(post("/v1/events")
                .willReturn(aResponse()
                        .withBodyFile("expected_event.json")));

        // Create the metrics client.
        ClientMetadata testJavaClientMetadata = createConsistencyTestClientMetadata();
        MetricsClient testJavaMetricsClient = MetricsClient.builder(testJavaClientMetadata)
                .streamConfigURL(new URL(wireMockRuntimeInfo.getHttpBaseUrl() + "/config"))
                .build();

        await().atMost(5, SECONDS).until(testJavaMetricsClient::isFullyInitialized);

        testJavaMetricsClient.dispatch(
                "eas.",
                singletonMap("action", "surf")
        );

        await().atMost(5, SECONDS).until(testJavaMetricsClient::isEventQueueEmpty);

        verify(postRequestedFor(urlEqualTo("/v1/events"))
                .withRequestBody(equalToJson(getExpectedEvent(), false, true)));
    }

    private byte[] readConfig() throws IOException {
        return ByteStreams.toByteArray(EndToEndIT.class.getClassLoader().getResourceAsStream("org/wikimedia/metrics_platform/config/streamconfigs-local.json"));
    }

    private String getExpectedEvent() throws IOException {
        Path pathExpectedEvent = Paths.get("src/test/resources/__files/expected_event.json");
        try (BufferedReader expectedEventReader = Files.newBufferedReader(pathExpectedEvent)) {
            JsonObject expectedEventJsonObject = JsonParser.parseReader(expectedEventReader).getAsJsonObject();
            return expectedEventJsonObject.toString();
        }
    }
}
