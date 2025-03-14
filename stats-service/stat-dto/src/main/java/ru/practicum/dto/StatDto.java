package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatDto {
    @NotBlank
    @Size(max = 50)
    private String app;

    @NotBlank
    @Size(max = 50)
    private String uri;

    private Set<String> uris;

    @NotBlank
    @Size(max = 15)
    private String ip;

    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime timestamp;
}