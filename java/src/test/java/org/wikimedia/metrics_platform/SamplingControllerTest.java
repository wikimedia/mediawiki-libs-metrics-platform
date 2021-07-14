package org.wikimedia.metrics_platform;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.wikimedia.metrics_platform.SamplingConfig.Identifier.DEVICE;
import static org.wikimedia.metrics_platform.SamplingConfig.Identifier.SESSION;

public class SamplingControllerTest {

    private final SamplingController samplingController = new SamplingController(
            new TestMetricsClientIntegration(),
            new SessionController()
    );

    @Test
    public void testGetSamplingValue() {
        double deviceVal = samplingController.getSamplingValue(DEVICE);
        assertThat(deviceVal, greaterThanOrEqualTo(0.0));
        assertThat(deviceVal, lessThanOrEqualTo(1.0));
    }

    @Test
    public void testGetSamplingId() {
        assertThat(samplingController.getSamplingId(DEVICE), is(notNullValue()));
        assertThat(samplingController.getSamplingId(SESSION), is(notNullValue()));
    }

    @Test
    public void testNoSamplingConfig() {
        StreamConfig noSamplingConfig = new StreamConfig("foo", "bar", null, null);
        assertThat(samplingController.isInSample(noSamplingConfig), is(true));
    }

    @Test
    public void testAlwaysInSample() {
        StreamConfig alwaysInSample = new StreamConfig("foo", "bar", null,
                new StreamConfig.ProducerConfig(new StreamConfig.ProducerConfig.MetricsPlatformClientConfig(
                        new SamplingConfig(1.0, null),
                        null)
                ));
        assertThat(samplingController.isInSample(alwaysInSample), is(true));
    }

    @Test
    public void testNeverInSample() {
        StreamConfig neverInSample = new StreamConfig("foo", "bar", null,
                new StreamConfig.ProducerConfig(new StreamConfig.ProducerConfig.MetricsPlatformClientConfig(
                        new SamplingConfig(0.0, null),
                        null)
                ));
        assertThat(samplingController.isInSample(neverInSample), is(false));
    }

}
