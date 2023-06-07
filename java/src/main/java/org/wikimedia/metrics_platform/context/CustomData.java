package org.wikimedia.metrics_platform.context;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
@ParametersAreNonnullByDefault
public class CustomData {
    @SerializedName("data_type") final CustomDataType type;
    final String value;

    /**
     * Return custom data, based on a generic object.
     *
     * @return formatted custom data
     */
    public static CustomData of(Object value) {
        String formattedValue = String.valueOf(value);
        CustomDataType formattedType;

        if (value instanceof Number) {
            formattedType = CustomDataType.NUMBER;
        } else if (value instanceof Boolean) {
            formattedType = CustomDataType.BOOLEAN;
            formattedValue = ((Boolean) value) ? "true" : "false";
        } else {
            formattedType = CustomDataType.STRING;
        }

        return new CustomData(formattedType, formattedValue);
    }

}
