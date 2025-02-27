package ru.practicum.dtos.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dtos.category.CategoryDto;
import ru.practicum.dtos.user.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventShortDto {
    Long id;
    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT)
    LocalDateTime eventDate;
    LocalDateTime publishedOn;
    UserShortDto initiator;
    Boolean paid;
    String title;
    Long views;
}
