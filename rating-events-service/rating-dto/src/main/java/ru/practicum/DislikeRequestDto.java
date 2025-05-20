package ru.practicum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DislikeRequestDto {

    @NotNull(message = "eventId must be not null")
    Long eventId;

    @NotNull(message = "userId must be not null")
    Long userId;
}
