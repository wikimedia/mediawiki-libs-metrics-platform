package org.wikimedia.metrics_platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.wikimedia.metrics_platform.config.sampling.SampleConfig.Identifier.DEVICE;
import static org.wikimedia.metrics_platform.config.sampling.SampleConfig.Identifier.SESSION;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.config.sampling.SampleConfig;
import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.context.AgentData;
import org.wikimedia.metrics_platform.context.ClientData;
import org.wikimedia.metrics_platform.context.DataFixtures;

class SamplingControllerTest {

    private final SamplingController samplingController = new SamplingController(
            DataFixtures.getTestClientData(),
            new SessionController()
    );

    @Test void testGetSamplingValue() {
        double deviceVal = samplingController.getSamplingValue(DEVICE);
        assertThat(deviceVal).isBetween(0.0, 1.0);
    }

    @Test void testGetSamplingId() {
        assertThat(samplingController.getSamplingId(DEVICE)).isNotNull();
        assertThat(samplingController.getSamplingId(SESSION)).isNotNull();
    }

    @Test void testNoSamplingConfig() {
        StreamConfig noSamplingConfig = new StreamConfig("foo", "bar", null, null, null);
        assertThat(samplingController.isInSample(noSamplingConfig)).isTrue();
    }

    @Test void testAlwaysInSample() {
        StreamConfig alwaysInSample = new StreamConfig("foo", "bar", null,
                new StreamConfig.ProducerConfig(new StreamConfig.MetricsPlatformClientConfig(
                        null,
                        null,
                        null
                )),
                null
        );
        assertThat(samplingController.isInSample(alwaysInSample)).isTrue();
    }

    @Test void testNeverInSample() {
        StreamConfig neverInSample = new StreamConfig("foo", "bar", null,
                new StreamConfig.ProducerConfig(new StreamConfig.MetricsPlatformClientConfig(
                        null,
                        null,
                        null
                )),
                new SampleConfig(0.0, SESSION)
        );
        assertThat(samplingController.isInSample(neverInSample)).isFalse();
    }

    @Test void testSamplingMonotonicity() {
        String deviceIdInSampleAt1Percent = findDeviceIdInSampleAtRate(0.01);
        assertThat(deviceIdInSampleAt1Percent).isNotNull();

        ClientData clientData = new ClientData(
                AgentData.builder()
                        .appInstallId(deviceIdInSampleAt1Percent)
                        .clientPlatform("android")
                        .clientPlatformFamily("app")
                        .build(),
                DataFixtures.getTestPageData(),
                DataFixtures.getTestMediawikiData(),
                DataFixtures.getTestPerformerData(),
                "en.wikipedia.org"
        );
        SamplingController controller = new SamplingController(clientData, new SessionController());

        double[] testRates = {0.01, 0.05, 0.10, 0.25, 0.50, 0.75, 1.0};
        for (double rate : testRates) {
            StreamConfig streamConfig = new StreamConfig("foo", "bar", null,
                    new StreamConfig.ProducerConfig(new StreamConfig.MetricsPlatformClientConfig(
                            null,
                            null,
                            null
                    )),
                    new SampleConfig(rate, DEVICE)
            );
            assertThat(controller.isInSample(streamConfig))
                    .withFailMessage("Device should be in-sample at rate " + (rate * 100) + "%")
                    .isTrue();
        }
    }

    private String findDeviceIdInSampleAtRate(double rate) {
        for (int i = 0; i < 10000; i++) {
            String deviceId = String.format(java.util.Locale.ROOT, "%08x-0000-0000-0000-000000000000", i);
            ClientData testClientData = new ClientData(
                    AgentData.builder()
                            .appInstallId(deviceId)
                            .clientPlatform("android")
                            .clientPlatformFamily("app")
                            .build(),
                    DataFixtures.getTestPageData(),
                    DataFixtures.getTestMediawikiData(),
                    DataFixtures.getTestPerformerData(),
                    "en.wikipedia.org"
            );
            SamplingController testController = new SamplingController(testClientData, new SessionController());
            if (testController.getSamplingValue(DEVICE) < rate) {
                return deviceId;
            }
        }
        return null;
    }

}
