package org.wikimedia.metrics_platform.context;

import com.google.gson.annotations.SerializedName;

public enum CustomDataType {
    @SerializedName("number") NUMBER,
    @SerializedName("string") STRING,
    @SerializedName("boolean") BOOLEAN,
    @SerializedName("null") NULL;
}
