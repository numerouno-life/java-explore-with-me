package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);
}
