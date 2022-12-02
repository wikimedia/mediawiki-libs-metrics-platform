package org.wikimedia.metrics_platform.config;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.google.common.io.ByteStreams.toByteArray;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@WireMockTest
public class StreamConfigIT {

    @Test void canLoadConfigOverHTTP(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        stubFor(get("/streamConfig").willReturn(
                aResponse()
                        .withBody(loadConfigStream())
                )
        );

        StreamConfigFetcher streamConfigFetcher = new StreamConfigFetcher(new URL(wmRuntimeInfo.getHttpBaseUrl() + "/streamConfig"));

        SourceConfig sourceConfig = streamConfigFetcher.fetchStreamConfigs();

        assertThat(sourceConfig).isNotNull();

    }

    private byte[] loadConfigStream() throws IOException {
        return toByteArray(StreamConfigFetcher.class.getClassLoader()
                .getResourceAsStream("org/wikimedia/metrics_platform/config/streamconfigs.json"));
    }

}
