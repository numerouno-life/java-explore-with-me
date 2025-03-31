package ru.practicum.dtos.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dtos.location.LocationDto;

import java.time.LocalDateTime;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEventDto {

    @NotBlank
    @NotNull
    @Size(min = 20, max = 2000)
    String annotation;
    @NotNull
    Long category;  // id категории к которой относится событие
    @NotBlank
    @Size(min = 20, max = 7000)
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT)
    @NotNull
    LocalDateTime eventDate;
    @NotNull
    LocationDto location;

    boolean paid;

    @PositiveOrZero
    Integer participantLimit = 0;

    boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120)
    String title;
}
