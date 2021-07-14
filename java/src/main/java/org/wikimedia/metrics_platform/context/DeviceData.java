package org.wikimedia.metrics_platform.context;

import com.google.gson.annotations.SerializedName;

/**
 * Device context data context fields.
 *
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
public class DeviceData {
    @SerializedName("pixel_ratio") private Float pixelRatio;
    @SerializedName("hardware_concurrency") private Integer hardwareConcurrency;
    @SerializedName("max_touch_points") private Integer maxTouchPoints;

    public DeviceData() { }

    public DeviceData(Builder builder) {
        this.pixelRatio = builder.pixelRatio;
        this.hardwareConcurrency = builder.hardwareConcurrency;
        this.maxTouchPoints = builder.maxTouchPoints;
    }

    public Float getPixelRatio() {
        return pixelRatio;
    }

    public void setPixelRatio(Float pixelRatio) {
        this.pixelRatio = pixelRatio;
    }

    public Integer getHardwareConcurrency() {
        return hardwareConcurrency;
    }

    public void setHardwareConcurrency(Integer hardwareConcurrency) {
        this.hardwareConcurrency = hardwareConcurrency;
    }

    public Integer getMaxTouchPoints() {
        return maxTouchPoints;
    }

    public void setMaxTouchPoints(Integer maxTouchPoints) {
        this.maxTouchPoints = maxTouchPoints;
    }

    public static class Builder {
        private Float pixelRatio;
        private Integer hardwareConcurrency;
        private Integer maxTouchPoints;

        public Builder pixelRatio(Float pixelRatio) {
            this.pixelRatio = pixelRatio;
            return this;
        }

        public Builder hardwareConcurrency(Integer hardwareConcurrency) {
            this.hardwareConcurrency = hardwareConcurrency;
            return this;
        }

        public Builder maxTouchPoints(Integer maxTouchPoints) {
            this.maxTouchPoints = maxTouchPoints;
            return this;
        }

        public DeviceData build() {
            return new DeviceData(this);
        }
    }
}
