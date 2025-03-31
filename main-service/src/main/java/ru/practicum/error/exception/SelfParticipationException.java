package ru.practicum.error.exception;

public class SelfParticipationException extends RuntimeException {
    public SelfParticipationException(String message) {
        super(message);
    }
}
