package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.Status;
import ru.practicum.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("""
            SELECT r.event.id, COUNT(r.id)
            FROM Request r
            WHERE r.status = 'CONFIRMED' AND r.event.id IN :eventIds
            GROUP BY r.event.id
            """)
    List<Object[]> countConfirmedRequestsByEventIds(@Param("eventIds") List<Long> eventIds);

    Long countByEventIdAndStatus(Long eventId, Status status);

    List<Request> findAllByEventIdInAndStatus(List<Long> eventIds, Status status);

    List<Request> findAllByEventInitiatorIdAndEventId(Long userId, Long eventId);
}
