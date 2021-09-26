package com.chat.producer.kafka;

import org.springframework.lang.Nullable;

import java.nio.charset.StandardCharsets;

public enum Operation {
    ADD,
    UPDATE,
    PATCH,
    REMOVE;

    @Nullable
    public static Operation fromBytes(byte[] bytes) {
        String value = new String(bytes, StandardCharsets.UTF_8);
        for (Operation operation : Operation.values()) {
            if (operation.name().equals(value)) {
                return operation;
            }
        }
        return null;
    }
}
