package org.wikimedia.metrics_platform;

import java.io.IOException;
import java.util.Map;

public interface StreamConfigsFetcher {
    Map<String, StreamConfig> fetchStreamConfigs() throws IOException;
}
