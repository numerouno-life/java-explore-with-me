package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.StatusComment;
import ru.practicum.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByAuthorIdAndStatusAndIsDeletedFalse(Long userId, StatusComment status);

    List<Comment> findAllByEventIdAndStatusAndIsDeletedFalse(Long eventId, StatusComment status,
                                                             PageRequest pageRequest);

    Optional<Comment> findByIdAndAuthorIdAndIsDeletedFalse(Long commentId, Long userId);

}
