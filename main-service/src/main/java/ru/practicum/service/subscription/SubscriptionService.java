package ru.practicum.service.subscription;

import ru.practicum.dtos.subscription.SubscriptionCheckDto;
import ru.practicum.dtos.subscription.SubscriptionFilter;
import ru.practicum.dtos.subscription.SubscriptionRequestDto;
import ru.practicum.dtos.subscription.SubscriptionResponseDto;

import java.util.List;

public interface SubscriptionService {

    SubscriptionResponseDto subscribe(Long userId, SubscriptionRequestDto request);

    void unsubscribe(Long userId, Long targetUserId);

    List<SubscriptionResponseDto> getSubscriptions(SubscriptionFilter filter);

    List<SubscriptionResponseDto> getSubscribers(SubscriptionFilter filter);

    Long countSubscriptions(Long userId);

    SubscriptionCheckDto getSubscriptionStatus(Long userId, Long targetUserId);

}
