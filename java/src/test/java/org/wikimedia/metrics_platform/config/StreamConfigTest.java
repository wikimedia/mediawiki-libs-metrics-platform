package org.wikimedia.metrics_platform.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.DestinationEventService;

import com.google.gson.Gson;

public class StreamConfigTest {

    @Test
    public void testStreamConfigDeserialization() {
        Gson gson = new Gson();
        String streamConfigJson = "{\"stream_name\":\"test.event\",\"schema_title\":\"test/event\",\"producers\":" +
                "{\"metrics_platform_client\":{\"provide_values\":[\"page_id\",\"user_id\"]}}," +
                "\"sample\":{\"rate\":0.5,\"identifier\":\"session\"}}";
        StreamConfig streamConfig = gson.fromJson(streamConfigJson, StreamConfig.class);
        assertThat(streamConfig.getStreamName()).isEqualTo("test.event");
        assertThat(streamConfig.getSchemaTitle()).isEqualTo("test/event");
        assertThat(streamConfig.getSampleConfig().getRate()).isEqualTo(0.5);
        assertThat(streamConfig.getSampleConfig().getIdentifier()).isEqualTo(SampleConfig.Identifier.SESSION);
        assertThat(streamConfig.getProducerConfig().getMetricsPlatformClientConfig().getRequestedValues())
                .isEqualTo(Arrays.asList("page_id", "user_id"));
        assertThat(streamConfig.getDestinationEventService()).isEqualTo(DestinationEventService.ANALYTICS);
    }
}
