package ru.practicum.service.subscription;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dtos.subscription.SubscriptionCheckDto;
import ru.practicum.dtos.subscription.SubscriptionFilter;
import ru.practicum.dtos.subscription.SubscriptionRequestDto;
import ru.practicum.dtos.subscription.SubscriptionResponseDto;
import ru.practicum.enums.FriendshipStatus;
import ru.practicum.error.exception.SubscriptionException;
import ru.practicum.mapper.SubscriptionMapper;
import ru.practicum.model.QSubscription;
import ru.practicum.model.Subscription;
import ru.practicum.model.User;
import ru.practicum.repository.SubscriptionRepository;
import ru.practicum.service.user.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper mapper;
    private final UserService userService;

    private static final Set<String> VALID_SORT_FIELDS = Set.of(
            "targetUser.name",
            "subscriptionTime",
            "friendshipStatus");
    private static final Set<String> VALID_SORT_FIELDS_SUBSCRIBERS = Set.of(
            "subscriber.name",
            "subscriptionTime",
            "friendshipStatus");

    @Override
    @Transactional
    public SubscriptionResponseDto subscribe(Long userId, SubscriptionRequestDto request) {
        log.info("Processing subscription request for user {} with data: {}", userId, request);
        User subscriber = userService.findUserById(userId);
        User targetUser = userService.findUserById(request.getTargetUserId());
        if (!subscriber.isAllowSubscriptions()) {
            log.warn("User {} does not allow subscriptions", userId);
            throw new SubscriptionException("User does not allow subscriptions");
        }
        if (subscriber.getId().equals(targetUser.getId())) {
            log.warn("User {} cannot subscribe to themselves", userId);
            throw new SubscriptionException("User cannot subscribe to themselves");
        }
        if (subscriptionRepository.existsBySubscriberAndTargetUser(subscriber, targetUser)) {
            log.warn("User {} already has a subscription to user {}", userId, request.getTargetUserId());
            throw new SubscriptionException("User already has a subscription to the target user");
        }
        Subscription subscription = mapper.toSubscription(subscriber, request);
        Optional<Subscription> existingReverseSubscription = subscriptionRepository
                .findBySubscriberAndTargetUser(targetUser, subscriber);
        if (existingReverseSubscription.isPresent()) {
            subscription.setFriendshipStatus(FriendshipStatus.MUTUAL);
            existingReverseSubscription.get().setFriendshipStatus(FriendshipStatus.MUTUAL);
            subscriptionRepository.saveAll(List.of(subscription, existingReverseSubscription.get()));
        } else {
            subscription.setFriendshipStatus(FriendshipStatus.ONE_WAY);
            subscriptionRepository.save(subscription);
        }
        log.info("Subscription created successfully for user {} to user {}. Friendship status: {}",
                userId, request.getTargetUserId(), subscription.getFriendshipStatus());
        return mapper.toSubscriptionResponseDto(subscription);
    }

    @Override
    @Transactional
    public void unsubscribe(Long userId, Long targetUserId) {
        log.info("Processing unsubscribe request from user {} to user {}", userId, targetUserId);
        User subscriber = userService.findUserById(userId);
        User targetUser = userService.findUserById(targetUserId);
        Optional<Subscription> subscription = subscriptionRepository
                .findBySubscriberAndTargetUser(subscriber, targetUser);
        if (subscription.isEmpty()) {
            log.warn("User {} does not have a subscription to user {}", userId, targetUserId);
            throw new SubscriptionException("User does not have a subscription to the target user");
        }
        Optional<Subscription> reverseSubscription = subscriptionRepository
                .findBySubscriberAndTargetUser(targetUser, subscriber);
        if (reverseSubscription.isPresent() &&
                reverseSubscription.get().getFriendshipStatus().equals(FriendshipStatus.MUTUAL)) {
            reverseSubscription.get().setFriendshipStatus(FriendshipStatus.ONE_WAY);
            subscriptionRepository.save(reverseSubscription.get());
        }
        subscriptionRepository.delete(subscription.get());
        log.info("Unsubscribe successful from user {} to user {}. Updated friendship status: {}",
                userId, targetUserId, reverseSubscription.map(Subscription::getFriendshipStatus).orElse(null));
    }

    // получить подписки пользователя
    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponseDto> getSubscriptions(SubscriptionFilter filter) {
        log.info("Getting subscriptions for user {}", filter.getUserId());
        validateSubscriptionFromAndSize(filter.getFrom(), filter.getSize());
        userService.findUserById(filter.getUserId());
        validateSubscriptionRequest(filter.getSortField(), false);

        QSubscription qSubscription = QSubscription.subscription;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qSubscription.subscriber.id.eq(filter.getUserId()));

        if (filter.getTargetUserName() != null && !filter.getTargetUserName().isBlank()) {
            builder.and(qSubscription.targetUser.name.containsIgnoreCase(filter.getTargetUserName()));
        }
        if (filter.getFriendshipStatus() != null) {
            builder.and(qSubscription.friendshipStatus.eq(filter.getFriendshipStatus()));
        }
        if (filter.getSubscriptionTime() != null) {
            builder.and(qSubscription.subscriptionTime.eq(filter.getSubscriptionTime()));
        }

        Pageable pageable = PageRequest.of(filter.getFrom() / filter.getSize(), filter.getSize(),
                filter.getSortDirection(), filter.getSortField());
        Page<Subscription> subscriptions = subscriptionRepository.findAll(builder, pageable);

        if (subscriptions.getContent().isEmpty()) {
            log.info("No subscriptions found for user {}", filter.getUserId());
        }

        return subscriptions.stream()
                .map(mapper::toSubscriptionResponseDto)
                .toList();
    }

    // получить подписчиков
    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponseDto> getSubscribers(SubscriptionFilter filter) {
        log.info("Getting subscribers for user:{}, from:{}, size:{}",
                filter.getUserId(), filter.getFrom(), filter.getSize());
        validateSubscriptionFromAndSize(filter.getFrom(), filter.getSize());
        userService.findUserById(filter.getUserId());
        validateSubscriptionRequest(filter.getSortField(), true);

        QSubscription qSubscriber = QSubscription.subscription;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qSubscriber.targetUser.id.eq(filter.getUserId()));
        if (filter.getTargetUserName() != null && !filter.getTargetUserName().isBlank()) {
            builder.and(qSubscriber.subscriber.name.containsIgnoreCase(filter.getTargetUserName()));
        }
        if (filter.getFriendshipStatus() != null) {
            builder.and(qSubscriber.friendshipStatus.eq(filter.getFriendshipStatus()));
        }
        if (filter.getSubscriptionTime() != null) {
            builder.and(qSubscriber.subscriptionTime.eq(filter.getSubscriptionTime()));
        }

        Pageable pageable = PageRequest.of(filter.getFrom() / filter.getSize(), filter.getSize(),
                filter.getSortDirection(), filter.getSortField());
        Page<Subscription> subscribers = subscriptionRepository.findAll(builder, pageable);

        if (subscribers.getContent().isEmpty()) {
            log.info("No subscribers found for user {}", filter.getUserId());
        }

        return subscribers.stream()
                .map(mapper::toSubscriptionResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countSubscriptions(Long userId) {
        return 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionCheckDto getSubscriptionStatus(Long userId, Long targetUserId) {
        return null;
    }

    /**
     * Проверяет, что поле для сортировки допустимо.
     * @param sortField поле для сортировки
     * @param isSubscribersRequest если true, проверяет поля для подписчиков, иначе – для подписок
     */
    private void validateSubscriptionRequest(String sortField, boolean isSubscribersRequest) {
        Set<String> validFields = isSubscribersRequest ? VALID_SORT_FIELDS : VALID_SORT_FIELDS_SUBSCRIBERS;
        if (!validFields.contains(sortField)) {
            log.warn("Invalid sort field: {}", sortField);
            throw new IllegalArgumentException("Invalid sort field: " + sortField);
        }
    }

    private void validateSubscriptionFromAndSize(int from, int size) {
        if (from < 0 || size <= 0) {
            log.warn("Invalid parameters 'from' and 'size'");
            throw new IllegalArgumentException("Parameters 'from' and 'size' must be positive");
        }
    }

}
