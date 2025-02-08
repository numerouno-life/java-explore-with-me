package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
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
                }
            } catch (Exception ignored) {

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
}