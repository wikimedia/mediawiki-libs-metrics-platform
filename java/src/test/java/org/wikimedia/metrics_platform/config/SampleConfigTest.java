package org.wikimedia.metrics_platform.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

public class SampleConfigTest {

    @Test
    public void testSamplingConfigDeserialization() {
        Gson gson = new Gson();
        String samplingConfigJson = "{\"rate\":0.25,\"identifier\":\"device\"}";
        SampleConfig sampleConfig = gson.fromJson(samplingConfigJson, SampleConfig.class);
        assertEquals(0.25, sampleConfig.getRate());
        assertEquals(SampleConfig.Identifier.DEVICE, sampleConfig.getIdentifier());
    }

}
