package org.wikimedia.metrics_platform;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

public class SamplingConfigTest {

    @Test
    public void testSamplingConfig() {
        SamplingConfig samplingConfig = new SamplingConfig(0.5, SamplingConfig.Identifier.DEVICE);
        assertThat(samplingConfig.getRate(), is(0.5));
        assertThat(samplingConfig.getIdentifier(), is(SamplingConfig.Identifier.DEVICE));
    }

    @Test
    public void testSamplingConfigDeserialization() {
        Gson gson = new Gson();
        String samplingConfigJson = "{\"rate\":0.25,\"identifier\":\"device\"}";
        SamplingConfig samplingConfig = gson.fromJson(samplingConfigJson, SamplingConfig.class);
        assertThat(samplingConfig.getRate(), is(0.25));
        assertThat(samplingConfig.getIdentifier(), is(SamplingConfig.Identifier.DEVICE));
    }

}
