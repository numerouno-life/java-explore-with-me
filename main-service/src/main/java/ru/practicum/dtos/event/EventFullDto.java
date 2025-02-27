package ru.practicum.dtos.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dtos.category.CategoryDto;
import ru.practicum.dtos.location.LocationDto;
import ru.practicum.dtos.user.UserShortDto;
import ru.practicum.enums.State;
import ru.practicum.model.Location;

import java.time.LocalDateTime;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {

    Long id;
    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    @JsonFormat(pattern = FORMAT)
    LocalDateTime createdOn;
    LocalDateTime publishedOn;
    String description;
    @JsonFormat(pattern = FORMAT)
    LocalDateTime eventDate;
    UserShortDto initiator;
    Location location;
    Boolean paid;
    Integer participantLimit;
    State state;
    Boolean requestModeration;
    String title;
    Long views;
}
