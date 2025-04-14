package ru.practicum.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StatusComment {
    CHECKING, PUBLISHED, REJECTED, DELETED;

    @JsonCreator
    public static StatusComment fromString(String status) {
        return StatusComment.valueOf(status.toUpperCase());
    }
}
