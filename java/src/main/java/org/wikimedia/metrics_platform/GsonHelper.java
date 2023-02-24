package org.wikimedia.metrics_platform;

import java.time.Instant;

import org.wikimedia.metrics_platform.context.CustomData;
import org.wikimedia.metrics_platform.context.CustomDataSerializer;
import org.wikimedia.metrics_platform.context.InstantConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonHelper {
    private GsonHelper() {
        // Utility class which should never be instantiated.
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(CustomData.class, new CustomDataSerializer())
                .registerTypeAdapter(Instant.class, new InstantConverter())
                .create();
    }
}
