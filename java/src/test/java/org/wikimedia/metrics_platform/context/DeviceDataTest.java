package org.wikimedia.metrics_platform.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

public class DeviceDataTest {

    @Test
    public void testDeviceData() {
        DeviceData deviceData = DeviceData.builder()
                .pixelRatio(1.0f)
                .hardwareConcurrency(1)
                .maxTouchPoints(1)
                .build();

        assertThat(deviceData.getPixelRatio()).isEqualTo(1.0f);
        assertThat(deviceData.getHardwareConcurrency()).isEqualTo(1);
        assertThat(deviceData.getMaxTouchPoints()).isEqualTo(1);

        Gson gson = new Gson();
        String json = gson.toJson(deviceData);
        assertThat(json).isEqualTo("{\"pixel_ratio\":1.0,\"hardware_concurrency\":1,\"max_touch_points\":1}");
    }

}
