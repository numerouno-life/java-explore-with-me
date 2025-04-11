package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.enums.StatusComment;
import ru.practicum.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    List<Comment> findAllByAuthorIdAndStatusAndIsDeletedFalse(Long userId, StatusComment status);

    List<Comment> findAllByEventIdAndStatusAndIsDeletedFalse(Long eventId, StatusComment status,
                                                             Pageable pageable);

    Optional<Comment> findByIdAndAuthorIdAndIsDeletedFalse(Long commentId, Long userId);

}