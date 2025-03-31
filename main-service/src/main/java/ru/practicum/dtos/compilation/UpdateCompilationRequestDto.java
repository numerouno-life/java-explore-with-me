package ru.practicum.dtos.compilation;

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
public class UpdateCompilationRequestDto {

    Set<Long> events;
    boolean pinned;
    @Size(min = 1, max = 50)
    String title;
}
