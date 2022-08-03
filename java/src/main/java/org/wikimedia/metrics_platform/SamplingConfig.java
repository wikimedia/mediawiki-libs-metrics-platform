package org.wikimedia.metrics_platform;

import com.google.gson.annotations.SerializedName;

import lombok.NonNull;
import lombok.Value;

@Value
class SamplingConfig {

    enum Identifier {
        @SerializedName("session") SESSION,
        @SerializedName("device") DEVICE
    }

    /** Sampling rate. **/
    private double rate;

    /** ID type to use for sampling. */
    @NonNull
    private Identifier identifier;

}
