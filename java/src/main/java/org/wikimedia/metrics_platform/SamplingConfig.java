package org.wikimedia.metrics_platform;

import com.google.gson.annotations.SerializedName;

class SamplingConfig {

    enum Identifier {
        @SerializedName("session") SESSION,
        @SerializedName("device") DEVICE
    }

    private double rate = 1.0;
    private Identifier identifier;

    // This constructor is needed for correct Gson deserialization. Do not remove!
    SamplingConfig() { }

    /**
     * Constructor for testing.
     *
     * In practice, field values will be set by Gson during deserialization using reflection.
     *
     * @param rate sampling rate
     * @param identifier ID type to use for sampling
     */
    SamplingConfig(double rate, Identifier identifier) {
        this.rate = rate;
        this.identifier = identifier;
    }

    double getRate() {
        return rate;
    }

    Identifier getIdentifier() {
        return identifier != null ? identifier : Identifier.SESSION;
    }

}
