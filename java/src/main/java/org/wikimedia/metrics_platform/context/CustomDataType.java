package org.wikimedia.metrics_platform.context;

import java.util.Locale;

public enum CustomDataType {
    NUMBER("number"),
    STRING("string"),
    BOOLEAN("boolean"),
    NULL("null");

    private final String name;

    CustomDataType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name.toLowerCase(Locale.ROOT);
    }
}
