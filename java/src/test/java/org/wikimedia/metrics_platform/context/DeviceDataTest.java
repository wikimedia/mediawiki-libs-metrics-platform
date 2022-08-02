package org.wikimedia.metrics_platform.context;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

public class DeviceDataTest {

    @Test
    public void testDeviceData() {
        DeviceData deviceData = new DeviceData.Builder()
                .pixelRatio(1.0f)
                .hardwareConcurrency(1)
                .maxTouchPoints(1)
                .build();

        assertThat(deviceData.getPixelRatio(), is(1.0f));
        assertThat(deviceData.getHardwareConcurrency(), is(1));
        assertThat(deviceData.getMaxTouchPoints(), is(1));

        Gson gson = new Gson();
        String json = gson.toJson(deviceData);
        assertThat(json, is("{\"pixel_ratio\":1.0,\"hardware_concurrency\":1,\"max_touch_points\":1}"));
    }

}
