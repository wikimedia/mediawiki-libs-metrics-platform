package org.wikimedia.metrics_platform.config;

import com.google.gson.annotations.SerializedName;

import lombok.NonNull;
import lombok.Value;

@Value
public class SampleConfig {

    public enum Identifier {
        @SerializedName("session") SESSION,
        @SerializedName("device") DEVICE,
        @SerializedName("unit") UNIT
    }

    /** Sampling rate. **/
    double rate;

    /** ID type to use for sampling. */
    @NonNull Identifier identifier;

    String unitValue;
}
