package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.enums.FriendshipStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    User subscriber;

    @ManyToOne
    @JoinColumn(name = "target_user_id")
    User targetUser;

    @Column(name = "subscription_time")
    @CreationTimestamp
    LocalDateTime subscriptionTime;

    @Column(name = "unsubscribe_time")
    LocalDateTime unsubscribeTime;

    @Column(name = "friendship_status", nullable = false)
    FriendshipStatus friendshipStatus = FriendshipStatus.ONE_WAY;

}