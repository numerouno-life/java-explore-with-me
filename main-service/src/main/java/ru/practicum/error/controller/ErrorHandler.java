package ru.practicum.error.controller;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.error.exception.*;
import ru.practicum.error.model.ApiError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestsException(Exception e) {
        log.warn(e.getMessage(), e);

        String errorMessage = "";
        List<String> errors = new ArrayList<>();
        String reason = "";
        Map<String, Object> context = null;

        if (e instanceof MethodArgumentNotValidException ex) {
            errors = ex.getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> String.format("Field '%s': %s", fieldError.getField(),
                            fieldError.getDefaultMessage()))
                    .toList();

            context = Map.of("invalidFieldsCount", errors.size());
            errorMessage = ex.getMessage();
        } else if (e instanceof MissingServletRequestParameterException ex) {
            errorMessage = String.format("Required parameter '%s' is missing", ex.getParameterName());
            reason = "MissingServletRequestParameterException";

            context = Map.of("missingParameter", ex.getParameterName());
        } else if (e instanceof ValidationException ex) {
            errorMessage = ex.getMessage();
            reason = "ValidationException";
            try {
                String[] parts = errorMessage.split(":");
                if (parts.length > 1) {
                    context = Map.of("entityId", parts[1].trim());
                    errorMessage = parts[0].trim();
                } else {
                    // Если сообщение не содержит ":", добавляем всё сообщение в контекст
                    context = Map.of("errorMessage", errorMessage);
                }
            } catch (Exception ignored) {
                // В случае ошибки всё равно добавляем сообщение в контекст
                context = Map.of("errorMessage", errorMessage);
            }
        }
        return ApiError.builder()
                .errors(errors)
                .message(errorMessage)
                .reason(reason)
                .status(HttpStatus.BAD_REQUEST.name())
                .localDateTime(LocalDateTime.now())
                .context(context)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final EntityNotFoundException e) {
        log.warn(e.getMessage(), e);
        List<String> errors = new ArrayList<>();
        return ApiError.builder()
                .errors(errors)
                .message(e.getMessage())
                .reason("EntityNotFoundException")
                .status(HttpStatus.NOT_FOUND.name())
                .localDateTime(LocalDateTime.now())
                .build();

    }

    @ExceptionHandler({DuplicateParticipationRequestException.class, InvalidStateException.class,
            SelfParticipationException.class, DataIntegrityViolationException.class, DuplicateCategoryException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final Exception e) {
        log.warn(e.getMessage(), e);
        String errorMessage;
        List<String> errors = new ArrayList<>();
        String reason;
        Map<String, Object> context;

        switch (e) {
            case DuplicateParticipationRequestException ex -> {
                errorMessage = String.format("Duplicate participation request for event with id=%s", ex.getMessage());
                reason = "DuplicateParticipationRequestException";
                context = Map.of("event", ex.getMessage());
            }
            case InvalidStateException ex -> {
                errorMessage = String.format("Invalid state: %s", ex.getMessage());
                reason = "InvalidStateException";
                context = Map.of("state", ex.getMessage());
            }
            case SelfParticipationException ex -> {
                errorMessage = "User cannot participate in their own event";
                reason = "SelfParticipationException";
                context = Map.of("user", ex.getMessage());
            }
            case DataIntegrityViolationException ex -> {
                errorMessage = "Data integrity violation occurred";
                reason = "DataIntegrityViolationException";
                String constraintName = extractConstraintName(ex);
                context = Map.of("constraint", constraintName, "message", ex.getMessage());
            }
            case DuplicateCategoryException ex -> {
                errorMessage = String.format("Category with name '%s' already exists", ex.getMessage());
                reason = "DuplicateCategoryException";
                context = Map.of("categoryName", ex.getMessage());
            }
            default -> {
                errorMessage = "Unexpected conflict error";
                reason = "ConflictException";
                context = Map.of("message", e.getMessage());
            }
        }

        return ApiError.builder()
                .errors(errors)
                .message(errorMessage)
                .reason(reason)
                .status(HttpStatus.CONFLICT.name())
                .localDateTime(LocalDateTime.now())
                .context(context)
                .build();
    }

    @ExceptionHandler(ParticipantLimitReachedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleParticipantLimitReached(ParticipantLimitReachedException ex) {
        log.warn(ex.getMessage(), ex);
        return ApiError.builder()
                .errors(List.of())
                .message(ex.getMessage())
                .reason("ParticipantLimitReachedException")
                .status(HttpStatus.CONFLICT.name())
                .localDateTime(LocalDateTime.now())
                .build();
    }


    private String extractConstraintName(DataIntegrityViolationException ex) {
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof ConstraintViolationException cvEx) {
            return cvEx.getConstraintName();
        }
        return "Unknown constraint";
    }


}
