package ru.practicum.controller.privates;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.subscription.SubscriptionCheckDto;
import ru.practicum.dtos.subscription.SubscriptionFilter;
import ru.practicum.dtos.subscription.SubscriptionRequestDto;
import ru.practicum.dtos.subscription.SubscriptionResponseDto;
import ru.practicum.enums.FriendshipStatus;
import ru.practicum.service.subscription.SubscriptionService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/subscriptions")
public class SubscriptionPrivateController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SubscriptionResponseDto> subscribe(@PathVariable Long userId,
                                                             @RequestBody @Valid SubscriptionRequestDto request) {
        log.info("POST: /users/{}/subscriptions", userId);
        SubscriptionResponseDto response = subscriptionService.subscribe(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{targetUserId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> unsubscribe(@PathVariable Long userId,
                                            @PathVariable Long targetUserId) {
        log.info("DELETE: /users/{}/subscriptions/targetUser{}", userId, targetUserId);
        subscriptionService.unsubscribe(userId, targetUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionResponseDto>> getSubscriptions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @Positive Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(required = false) String targetUserName,
            @RequestParam(defaultValue = "targetUser.name") String sortField,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
            @RequestParam(required = false) FriendshipStatus friendshipStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = FORMAT) LocalDateTime subTime
    ) {
        log.info("GET: /users/{}/subscriptions?from={}&size={}", userId, from, size);
        SubscriptionFilter filter = SubscriptionFilter.builder()
                .userId(userId)
                .from(from)
                .size(size)
                .targetUserName(targetUserName)
                .sortField(sortField)
                .sortDirection(sortDirection)
                .friendshipStatus(friendshipStatus)
                .subscriptionTime(subTime)
                .build();

        List<SubscriptionResponseDto> subscriptions = subscriptionService.getSubscriptions(filter);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/subscribers")
    public ResponseEntity<List<SubscriptionResponseDto>> getSubscribers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @Positive Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(required = false) String targetUserName,
            @RequestParam(defaultValue = "targetUser.name") String sortField,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
            @RequestParam(required = false) FriendshipStatus friendshipStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = FORMAT) LocalDateTime subTime) {
        log.info("GET: /users/{}/subscriptions/subscribers?from={}&size={}", userId, from, size);
        SubscriptionFilter filter = SubscriptionFilter.builder()
                .userId(userId)
                .from(from)
                .size(size)
                .targetUserName(targetUserName)
                .sortField(sortField)
                .sortDirection(sortDirection)
                .friendshipStatus(friendshipStatus)
                .subscriptionTime(subTime)
                .build();

        List<SubscriptionResponseDto> subscribers = subscriptionService.getSubscribers(filter);
        return ResponseEntity.ok(subscribers);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countSubscriptions(@PathVariable Long userId) {
        log.info("GET: /users/{}/subscriptions/count", userId);
        Long count = subscriptionService.countSubscriptions(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/status/{targetUserId}")
    public ResponseEntity<SubscriptionCheckDto> getSubscriptionStatus(@PathVariable Long userId,
                                                                      @PathVariable Long targetUserId) {
        log.info("GET: /users/{}/subscriptions/status/{}", userId, targetUserId);
        SubscriptionCheckDto isSubscribed = subscriptionService.getSubscriptionStatus(userId, targetUserId);
        return ResponseEntity.ok(isSubscribed);
    }

}