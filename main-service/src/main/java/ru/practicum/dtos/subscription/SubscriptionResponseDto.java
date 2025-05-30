package ru.practicum.dtos.subscription;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dtos.user.UserDto;
import ru.practicum.enums.FriendshipStatus;

import java.time.LocalDateTime;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponseDto {

    Long id;
    Long subscriberId;
    UserDto targetUser;

    @NotNull
    @JsonFormat(pattern = FORMAT)
    LocalDateTime subscriptionTime;

    @Nullable
    @JsonFormat(pattern = FORMAT)
    LocalDateTime unsubscribeTime;

    FriendshipStatus friendshipStatus;
}
