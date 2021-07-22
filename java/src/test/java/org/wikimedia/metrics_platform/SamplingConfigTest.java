package org.wikimedia.metrics_platform;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SamplingConfigTest {

    @Test
    public void testSamplingConfig() {
        SamplingConfig samplingConfig = new SamplingConfig(0.5, SamplingConfig.Identifier.DEVICE);
        assertThat(samplingConfig.getRate(), is(0.5));
        assertThat(samplingConfig.getIdentifier(), is(SamplingConfig.Identifier.DEVICE));
    }

}
