package ru.practicum.error.exception;

public class InvalidStateException extends RuntimeException{
    public InvalidStateException(String message) {
        super(message);
    }
}
