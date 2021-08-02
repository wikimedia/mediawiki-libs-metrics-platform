package org.wikimedia.metrics_platform;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class StreamConfigTest {

    @Test
    public void testStreamConfig() {
        SamplingConfig samplingConfig = new SamplingConfig();
        StreamConfig streamConfig = new StreamConfig(
                "test.event",
                "test/event",
                DestinationEventService.ANALYTICS,
                new StreamConfig.ProducerConfig(new StreamConfig.ProducerConfig.MetricsPlatformClientConfig(
                        samplingConfig
                ))
        );

        assertThat(streamConfig.getStreamName(), is("test.event"));
        assertThat(streamConfig.getSchemaTitle(), is("test/event"));
        assertThat(streamConfig.getProducerConfig().getMetricsPlatformClientConfig().getSamplingConfig(), is(samplingConfig));
        assertThat(streamConfig.getDestinationEventService(), is(DestinationEventService.ANALYTICS));
    }

}
