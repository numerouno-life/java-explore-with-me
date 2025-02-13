package ru.practicum.dtos.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dtos.category.CategoryDto;
import ru.practicum.dtos.location.LocationDto;
import ru.practicum.dtos.user.UserShortDto;
import ru.practicum.enums.State;

import java.time.LocalDateTime;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {

    Integer id;
    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT)
    LocalDateTime createdOn;
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT)
    LocalDateTime eventDate;
    UserShortDto initiator;
    LocationDto location;
    boolean paid;
    Integer participantLimit;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT)
    LocalDateTime publishedOn;
    boolean requestModeration;
    State state;
    String title;
    Integer views;

}
