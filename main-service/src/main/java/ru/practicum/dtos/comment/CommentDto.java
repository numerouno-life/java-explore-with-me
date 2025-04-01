package ru.practicum.dtos.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.StatusComment;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    Long id;
    String text;
    String authorName;
    Long eventId;

    @JsonFormat(pattern = FORMAT)
    LocalDateTime created;

    @JsonFormat(pattern = FORMAT)
    LocalDateTime updated;

    StatusComment status; // Статус комментария
    Boolean isDeleted;    // Флаг удаления
    List<CommentDto> replies; // Список дочерних комментариев (ответов)
}
