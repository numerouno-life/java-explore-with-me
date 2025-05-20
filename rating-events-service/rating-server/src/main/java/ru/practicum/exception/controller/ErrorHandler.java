package ru.practicum.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.exception.LikeAlreadyExistsException;
import ru.practicum.exception.exception.LikeNotFoundException;
import ru.practicum.exception.model.ApiError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final LikeNotFoundException e) {
        log.warn(e.getMessage(), e);
        List<String> errors = new ArrayList<>();
        return ApiError.builder()
                .errors(errors)
                .message(e.getMessage())
                .reason("LIKE_NOT_FOUND_EXCEPTION")
                .status(HttpStatus.NOT_FOUND.name())
                .localDateTime(LocalDateTime.now())
                .build();

    }

    @ExceptionHandler(LikeAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleLikeAlreadyExistsException(LikeAlreadyExistsException ex) {
        log.warn(ex.getMessage(), ex);
        return ApiError.builder()
                .errors(List.of())
                .message(ex.getMessage())
                .reason("LIKE_ALREADY_EXISTS_EXCEPTION")
                .status(HttpStatus.CONFLICT.name())
                .localDateTime(LocalDateTime.now())
                .build();
    }


}
