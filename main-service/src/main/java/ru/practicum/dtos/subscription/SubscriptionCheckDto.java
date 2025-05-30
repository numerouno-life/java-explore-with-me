package ru.practicum.dtos.subscription;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.FriendshipStatus;

import java.time.LocalDateTime;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionCheckDto {

    @Nullable
    @JsonFormat(pattern = FORMAT)
    LocalDateTime subscriptionTime;

    FriendshipStatus friendshipStatus;
}
