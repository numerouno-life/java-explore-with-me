package ru.practicum.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.Status;

import java.time.LocalDateTime;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequestDto {
    Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT)
    LocalDateTime created;
    Long event;
    Long requester;
    Status status;
}
