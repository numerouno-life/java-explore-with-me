package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.Status;

import java.time.LocalDateTime;

@Entity
@Table(name = "participation_requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    Long id;
    @Builder.Default
    LocalDateTime created = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;
    @Enumerated(value = EnumType.STRING)
    Status status;

}
