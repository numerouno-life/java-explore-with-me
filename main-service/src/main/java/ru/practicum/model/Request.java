package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dtos.request.ParticipationRequestDto;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    Long id;

    @Column(name = "created_on", nullable = false)
    LocalDateTime created;

    @JoinColumn(name = "event_id")
    Event event;

    @Enumerated(EnumType.STRING)
    ParticipationRequestDto.Status status;

    @JoinColumn(name = "requestor_id")
    User requester;
}
