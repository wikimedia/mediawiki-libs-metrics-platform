package org.wikimedia.metrics_platform.config.sampling;

import javax.annotation.Nonnull;

import com.google.gson.annotations.SerializedName;

import lombok.Value;

@Value
public class SampleConfig {

    public enum Identifier {
        @SerializedName("session") SESSION,
        @SerializedName("device") DEVICE,
        @SerializedName("pageview") PAGEVIEW
    }

    /** Sampling rate. **/
    double rate;

    /** ID type to use for sampling. */
    @Nonnull Identifier identifier;
}
