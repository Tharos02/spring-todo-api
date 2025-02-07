package de.till.todo.task;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    TODO(0),
    IN_PROGRESS(1),
    DONE(2);

    private final int value;

    Status(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
