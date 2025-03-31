package ru.practicum.dtos.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationRequest {
    Set<Long> events;
    Boolean pinned;

    @Size(min = 1, max = 50)
    String title;
}
