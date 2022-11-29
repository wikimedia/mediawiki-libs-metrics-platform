package org.wikimedia.metrics_platform.context;

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
        return name;
    }
}
