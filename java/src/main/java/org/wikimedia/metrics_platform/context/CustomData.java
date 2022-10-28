package org.wikimedia.metrics_platform.context;

import static java.lang.Boolean.parseBoolean;

import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Data;

@Data
@ParametersAreNonnullByDefault
public class CustomData {
    private final String name;
    private final CustomDataType type;
    private final String value;

    /**
     * Return custom data as a raw key/value pair.
     *
     * @return raw custom data
     */
    public static CustomData of(Map.Entry<String, Object> e) {
        return of(e.getKey(), e.getValue());
    }

    /**
     * Return custom data as a formatted key/value pair.
     *
     * @return formatted custom data
     */
    public static CustomData of(String key, Object value) {
        String formattedValue = String.valueOf(value);
        CustomDataType formattedType;

        if (value instanceof Number) {
            formattedType = CustomDataType.NUMBER;
        } else if (value instanceof String) {
            formattedType = CustomDataType.STRING;
        } else if (value instanceof Boolean) {
            formattedType = CustomDataType.BOOLEAN;
            formattedValue = parseBoolean(formattedValue) ? "true" : "false";
        } else {
            formattedType = CustomDataType.STRING;
        }

        return new CustomData(key, formattedType, formattedValue);
    }

}
