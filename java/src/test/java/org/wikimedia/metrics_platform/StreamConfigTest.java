package org.wikimedia.metrics_platform;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
                        samplingConfig,
                        null
                ))
        );

        assertThat(streamConfig.getStreamName(), is("test.event"));
        assertThat(streamConfig.getSchemaTitle(), is("test/event"));
        assertThat(streamConfig.getProducerConfig().getMetricsPlatformClientConfig().getSamplingConfig(), is(samplingConfig));
        assertThat(streamConfig.getDestinationEventService(), is(DestinationEventService.ANALYTICS));
    }

    @Test
    public void testStreamConfigDeserialization() {
        Gson gson = new Gson();
        String streamConfigJson = "{\"stream\":\"test.event\",\"schema_title\":\"test/event\",\"producers\":" +
                "{\"metrics_platform_client\":{\"sampling\":{\"rate\":0.5,\"identifier\":\"session\"}," +
                "\"provide_values\":[\"page_id\",\"user_id\"]}}}";

        StreamConfig streamConfig = gson.fromJson(streamConfigJson, StreamConfig.class);
        assertThat(streamConfig.getStreamName(), is("test.event"));
        assertThat(streamConfig.getSchemaTitle(), is("test/event"));
        assertThat(streamConfig.getProducerConfig().getMetricsPlatformClientConfig().getSamplingConfig().getRate(), is(0.5));
        assertThat(streamConfig.getProducerConfig().getMetricsPlatformClientConfig().getSamplingConfig().getIdentifier(), is(SamplingConfig.Identifier.SESSION));
        assertThat(streamConfig.getProducerConfig().getMetricsPlatformClientConfig().getRequestedValues(), is(Arrays.asList("page_id", "user_id")));
        assertThat(streamConfig.getDestinationEventService(), is(DestinationEventService.ANALYTICS));
    }

}
