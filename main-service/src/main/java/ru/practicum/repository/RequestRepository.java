package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Request r WHERE r.event.id = :eventId AND r.requester.id = :userId")
    boolean existsByEventIdAndRequesterId(@Param("eventId") Long eventId, @Param("userId") Long userId);

    List<Request> findByEventId(long eventId);

    List<Request> findAllByRequesterId(Long userId);

    Integer countByEventId(Integer eventId);

}
