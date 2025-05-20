package ru.practicum;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingResponseDto {

    private Long likes;
    private Long dislikes;
    private Long totalRating;
}
