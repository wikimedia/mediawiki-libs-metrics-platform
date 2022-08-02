package org.wikimedia.metrics_platform;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.junit.jupiter.api.Test;

public class MetricsClientIntegrationTest {

    @Test
    public void testMetricsClientIntegration() {
        MetricsClientIntegration integration = new TestMetricsClientIntegration();
        MetricsClientIntegration.FetchStreamConfigsCallback fetchStreamConfigsCallback =
                mock(MetricsClientIntegration.FetchStreamConfigsCallback.class);
        MetricsClientIntegration.SendEventsCallback sendEventsCallback =
                mock(MetricsClientIntegration.SendEventsCallback.class);

        integration.fetchStreamConfigs(fetchStreamConfigsCallback);
        integration.sendEvents("https://fake.url", Collections.emptyList(), sendEventsCallback);

        assertThat(integration.getAppInstallId(), is("6f31a4fa-0a77-4c65-9994-f242fa58ce94"));
        verify(fetchStreamConfigsCallback, times(1))
                .onSuccess(TestMetricsClientIntegration.STREAM_CONFIGS);
        verify(sendEventsCallback, times(1)).onSuccess();
    }

}
