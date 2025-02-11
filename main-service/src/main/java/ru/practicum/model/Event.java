package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dtos.event.EventFullDto;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    Long id;

    String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne
    @JoinColumn(name = "location_id")
    Location location;

    String description;

    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;

    Boolean paid;

    @Column(name = "participant_limit", nullable = false)
    Integer participantLimit;

    Boolean requestModeration;

    @Column(name = "confirmed_request", nullable = false)
    Integer confirmedRequest;

    String title;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    User initiator;

    @Enumerated(EnumType.STRING)
    EventFullDto.State state;

    Integer views;

    @Column(name = "created_on", nullable = false)
    LocalDateTime created;

    @Column(name = "published_on", nullable = false)
    LocalDateTime published;

}
