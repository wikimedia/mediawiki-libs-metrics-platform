package org.wikimedia.metrics_platform;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonMap;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.wikimedia.metrics_platform.ConsistencyITClientData.createConsistencyTestClientData;

import java.io.IOException;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.context.ClientData;
import org.wikimedia.metrics_platform.context.PageDataFixtures;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.common.io.Resources;

@WireMockTest(httpPort = 8192)
public class EndToEndIT {
    private String expectedEvent;

    @Test void submitEventTimerStreamConfig(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException {
        // Stub fetching the stream config from api endpoint.
        stubFor(get(urlEqualTo("/config"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(readConfig())));

        // Stub response from posting event to local eventgate logging service.
        stubFor(post("/v1/events?hasty=true")
                .willReturn(aResponse()
                        .withBody(getExpectedEvent())));

        // Create the metrics client.
        ClientData testJavaClientData = createConsistencyTestClientData();
        MetricsClient testJavaMetricsClient = MetricsClient.builder(testJavaClientData)
                .streamConfigURL(new URL(wireMockRuntimeInfo.getHttpBaseUrl() + "/config"))
                .build();

        await().atMost(5, SECONDS).until(testJavaMetricsClient::isFullyInitialized);

        testJavaMetricsClient.submitMetricsEvent(
                "eas.test_event_name_for_end_to_end_testing",
                PageDataFixtures.getTestPageData(getExpectedEvent()),
                singletonMap("action", "surf")
        );

        await().atMost(5, SECONDS).until(testJavaMetricsClient::isEventQueueEmpty);

        verify(postRequestedFor(urlEqualTo("/v1/events?hasty=true"))
                .withRequestBody(equalToJson(getExpectedEvent(), true, true)));
    }

    private byte[] readConfig() throws IOException {
        return Resources.asByteSource(
                Resources.getResource("org/wikimedia/metrics_platform/config/streamconfigs-local.json")
        ).read();
    }

    private String getExpectedEvent() throws IOException {
        if (this.expectedEvent == null) {
            this.expectedEvent = Resources.asCharSource(
                    Resources.getResource("org/wikimedia/metrics_platform/event/expected_event.json"),
                    UTF_8
            ).read();
        }
        return this.expectedEvent;
    }
}
