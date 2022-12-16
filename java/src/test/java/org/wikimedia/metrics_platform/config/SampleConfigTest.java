package org.wikimedia.metrics_platform.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.wikimedia.metrics_platform.config.SampleConfig.Identifier.DEVICE;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

class SampleConfigTest {

    @Test void testSamplingConfigDeserialization() {
        Gson gson = new Gson();
        String samplingConfigJson = "{\"rate\":0.25,\"identifier\":\"device\"}";
        SampleConfig sampleConfig = gson.fromJson(samplingConfigJson, SampleConfig.class);
        assertThat(sampleConfig.getRate()).isEqualTo(0.25);
        assertThat(sampleConfig.getIdentifier()).isEqualTo(DEVICE);
    }

}
