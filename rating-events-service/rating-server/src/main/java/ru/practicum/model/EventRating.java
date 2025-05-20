package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_ratings")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class EventRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_rating_id")
    Long eventRatingId;

    @Column(name = "event_id", nullable = false)
    Long eventId;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "type", nullable = false)
    String type;

    @Column(name = "created", nullable = false)
    @CreationTimestamp
    LocalDateTime created;
}
