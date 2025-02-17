package ru.practicum.error.exception;

public class DuplicateParticipationRequestException extends RuntimeException {
    public DuplicateParticipationRequestException(String message) {
        super(message);
    }
}
