package org.wikimedia.metrics_platform.context;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Device context data context fields.
 *
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
@Builder @Data @NoArgsConstructor @AllArgsConstructor
public class DeviceData {
    @SerializedName("pixel_ratio") private Float pixelRatio;
    @SerializedName("hardware_concurrency") private Integer hardwareConcurrency;
    @SerializedName("max_touch_points") private Integer maxTouchPoints;
}
