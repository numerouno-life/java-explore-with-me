package ru.practicum.dtos.subscription;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRequestDto {

    @NotNull
    @Positive(message = "userId must be positive")
    Long targetUserId;
}
