package org.wikimedia.metrics_platform.context;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CustomDataSerializer implements JsonSerializer<CustomData> {
    public JsonElement serialize(CustomData src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonCustomData = new JsonObject();
        JsonObject properties = new JsonObject();
        properties.addProperty("data_type", src.getType().toString());
        properties.addProperty("value", src.getValue());
        jsonCustomData.add(src.getName(), properties);
        return jsonCustomData;
    }
}
