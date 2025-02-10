package ru.practicum.dtos.compilation;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dtos.event.EventShortDto;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {

    Integer id;
    boolean pinned;
    String title;
    List<EventShortDto> events;
}
