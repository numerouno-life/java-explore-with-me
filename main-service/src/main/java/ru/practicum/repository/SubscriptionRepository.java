package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.Subscription;
import ru.practicum.model.User;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>,
        QuerydslPredicateExecutor<Subscription> {

    Optional<Subscription> findBySubscriberAndTargetUser(User subscriber, User targetUser);

    boolean existsBySubscriberAndTargetUser(User subscriber, User targetUser);
}
