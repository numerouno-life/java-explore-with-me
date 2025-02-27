package ru.practicum.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT)
    LocalDateTime created;

    @NotNull
    Long event; // Идентификатор события

    @NotNull
    Long requester; // Идентификатор пользователя

    @NotNull
    Status status;

}
