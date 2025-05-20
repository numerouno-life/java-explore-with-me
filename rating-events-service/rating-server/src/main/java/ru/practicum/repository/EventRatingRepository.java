package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.EventRating;

public interface EventRatingRepository extends JpaRepository<EventRating, Long> {

    Boolean existsByEventIdAndUserIdAndType(Long eventId, Long userId, String type);

    void deleteByEventIdAndUserIdAndType(Long eventId, Long userId, String type);

    long countByEventIdAndType(Long eventId, String type);
}
