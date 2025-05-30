package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dtos.subscription.SubscriptionCheckDto;
import ru.practicum.dtos.subscription.SubscriptionRequestDto;
import ru.practicum.dtos.subscription.SubscriptionResponseDto;
import ru.practicum.model.Subscription;
import ru.practicum.model.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubscriptionMapper {

    SubscriptionResponseDto toSubscriptionResponseDto(Subscription subscription);

    Subscription toSubscription(User subscriber, SubscriptionRequestDto subscriptionRequestDto);

    default SubscriptionCheckDto toSubscriptionCheckDto(Subscription subscription) {
        return SubscriptionCheckDto.builder()
                .subscriptionTime(subscription != null ? subscription.getSubscriptionTime() : null)
                .friendshipStatus(subscription != null ? subscription.getFriendshipStatus() : null)
                .build();
    }
}
