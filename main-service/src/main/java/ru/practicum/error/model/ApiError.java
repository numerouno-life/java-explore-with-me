package ru.practicum.error.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    @Builder.Default
    private List<String> errors = new ArrayList<>();
    private String message;
    private String reason; // причина ошибки
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT)
    private LocalDateTime localDateTime;

    private Map<String, Object> context;
}