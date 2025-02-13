package ru.practicum.dtos.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dtos.location.LocationDto;
import ru.practicum.enums.StateAction;

import java.time.LocalDateTime;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventUserRequest {

    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;

    @Positive
    Integer category; // новая категория

    @NotBlank
    @Size(min = 20, max = 7000)
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT)
    LocalDateTime eventDate;

    @NotNull
    LocationDto location;

    boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    boolean requestModeration;
    StateAction stateAction;

    @NotBlank
    @Size(min = 3, max = 120)
    String title;
}
