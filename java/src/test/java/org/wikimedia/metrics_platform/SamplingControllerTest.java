package org.wikimedia.metrics_platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.wikimedia.metrics_platform.config.SampleConfig.Identifier.DEVICE;
import static org.wikimedia.metrics_platform.config.SampleConfig.Identifier.SESSION;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.config.SampleConfig;
import org.wikimedia.metrics_platform.config.StreamConfig;

public class SamplingControllerTest {

    private final SamplingController samplingController = new SamplingController(
            new TestClientMetadata(),
            new SessionController()
    );

    @Test
    public void testGetSamplingValue() {
        double deviceVal = samplingController.getSamplingValue(DEVICE);
        assertThat(deviceVal).isBetween(0.0, 1.0);
    }

    @Test
    public void testGetSamplingId() {
        assertThat(samplingController.getSamplingId(DEVICE)).isNotNull();
        assertThat(samplingController.getSamplingId(SESSION)).isNotNull();
    }

    @Test
    public void testNoSamplingConfig() {
        StreamConfig noSamplingConfig = new StreamConfig("foo", "bar", null, null, null);
        assertThat(samplingController.isInSample(noSamplingConfig)).isEqualTo(true);
    }

    @Test
    public void testAlwaysInSample() {
        StreamConfig alwaysInSample = new StreamConfig("foo", "bar", null,
                new StreamConfig.ProducerConfig(new StreamConfig.MetricsPlatformClientConfig(
                        null,
                        null,
                        null
                )),
                null
        );
        assertThat(samplingController.isInSample(alwaysInSample)).isEqualTo(true);
    }

    @Test
    public void testNeverInSample() {
        StreamConfig neverInSample = new StreamConfig("foo", "bar", null,
                new StreamConfig.ProducerConfig(new StreamConfig.MetricsPlatformClientConfig(
                        null,
                        null,
                        null
                )),
                new SampleConfig(0.0, SESSION, null)
        );
        assertThat(samplingController.isInSample(neverInSample)).isEqualTo(false);
    }

}
