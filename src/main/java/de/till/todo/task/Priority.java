package de.till.todo.task;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Priority {
    LOW(0),
    MEDIUM(1),
    HIGH(2);

    private final int value;

    Priority(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}