package ru.practicum.dtos.compilation;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dtos.event.EventShortDto;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {

    Long id;
    Boolean pinned;
    @Size(min = 1, max = 50)
    String title;
    Set<EventShortDto> events;
}
