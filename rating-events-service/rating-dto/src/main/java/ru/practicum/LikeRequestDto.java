package ru.practicum;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeRequestDto {

    @NotNull(message = "eventId must be not null")
    Long eventId;

    @NotNull(message = "userId must be not null")
    Long userId;
}
