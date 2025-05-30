package ru.practicum.dtos.subscription;

import lombok.*;
import org.springframework.data.domain.Sort;
import ru.practicum.enums.FriendshipStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class SubscriptionFilter {
    private Long userId;
    private Integer from;
    private Integer size;
    private String targetUserName;
    private String sortField;
    private Sort.Direction sortDirection;
    private FriendshipStatus friendshipStatus;
    private LocalDateTime subscriptionTime;
}
