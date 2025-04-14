package ru.practicum.service.comment;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dtos.comment.CommentDto;
import ru.practicum.dtos.comment.NewCommentDto;
import ru.practicum.dtos.comment.StatusUpdateDto;
import ru.practicum.dtos.comment.UpdateCommentDto;
import ru.practicum.enums.State;
import ru.practicum.enums.StatusComment;
import ru.practicum.error.exception.EntityNotFoundException;
import ru.practicum.error.exception.SelfParticipationException;
import ru.practicum.error.exception.ValidationException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.QComment;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    /**
     * Поиск комментариев по фильтру. Возможны поиску по тексту и по статусу комментария.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentsByFilter(String text, StatusComment statusComment,
                                                   Integer from, Integer size) {
        log.info("Getting comments by filter");
        QComment comment = QComment.comment;
        BooleanBuilder builder = new BooleanBuilder();
        if (text != null && !text.isBlank()) {
            builder.and(comment.text.containsIgnoreCase(text));
        }
        if (statusComment != null) {
            builder.and(comment.status.eq(statusComment));
            // Если статус НЕ DELETED, то проверяем isDeleted == false
            if (statusComment != StatusComment.DELETED) {
                builder.and(comment.isDeleted.eq(false));
            }
        } else {
            // если статус не задан, то исключаем мягко удалённые комментарии
            builder.and(comment.isDeleted.eq(false));
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "created"));
        Page<Comment> commentsPage = commentRepository.findAll(builder, pageable);
        List<Comment> allComments = new ArrayList<>(commentsPage.getContent());
        for (Comment c : commentsPage.getContent()) {
            allComments.addAll(findAllReplyComments(c, Objects.requireNonNull(statusComment)));
        }
        return allComments.stream()
                .map(commentMapper::mapToCommentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CommentDto getCommentByIdFromAdmin(Long commentId) {
        log.info("Getting comment by ID {} from Admin", commentId);
        return commentMapper.mapToCommentDto(createCommentById(commentId));
    }

    @Transactional
    @Override
    public CommentDto updateCommentStatusByAdmin(Long commentId, StatusUpdateDto status) {
        log.info("Updating status of comment with ID {} by admin", commentId);
        Comment comment = createCommentById(commentId);
        if (comment.getStatus().equals(StatusComment.PUBLISHED)) {
            log.warn("Comment with ID {} is not in CHECKING state. Cannot update status", commentId);
            throw new ValidationException("Comment is not in CHECKING state. Cannot update status");
        }
        comment.setStatus(status.getStatus());
        commentRepository.save(comment);
        log.info("Status of comment with ID {} has been updated successfully", commentId);
        return commentMapper.mapToCommentDto(comment);
    }

    @Transactional
    @Override
    public void deleteCommentByIdFromAdmin(Long commentId) {
        log.info("Deleting comment with ID {} from admin", commentId);
        Comment comment = createCommentById(commentId);
        comment.setIsDeleted(true);
        commentRepository.save(comment);
        comment.setStatus(StatusComment.DELETED);
        log.info("Comment with ID {} has been soft-deleted by admin successfully", commentId);
    }

    @Transactional
    @Override
    public void hardDeleteComment(Long commentId) {
        log.info("Hard deleting comment with ID {}", commentId);
        commentRepository.delete(createCommentById(commentId));
        log.info("Comment with ID {} has been hard-deleted by admin successfully", commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsAboutEvent(Long eventId, Integer from, Integer size) {
        log.info("Getting all comments about event with ID {}", eventId);
        Event event = createEventById(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            log.warn("Event with ID {} is not published. Cannot get comments", eventId);
            throw new ValidationException("Event is not published. Cannot get comments");
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findAllByEventIdAndStatusAndIsDeletedFalse(
                eventId, StatusComment.PUBLISHED, pageable);
        return Optional.of(comments)
                .filter(c -> !c.isEmpty())
                .map(c -> c.stream()
                        .map(commentMapper::mapToCommentDto)
                        .toList()).orElse(Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getUserAuthoredComments(Long userId) {
        log.info("Getting all comments about user with ID {}", userId);
        checkUserExists(userId);
        List<Comment> comments = commentRepository.findAllByAuthorIdAndStatusAndIsDeletedFalse(
                userId, StatusComment.PUBLISHED);
        return Optional.of(comments)
                .filter(c -> !c.isEmpty())
                .map(c -> c.stream()
                        .map(commentMapper::mapToCommentDto)
                        .toList()).orElse(Collections.emptyList());
    }

    @Override
    public CommentDto getCommentById(Long userId, Long commentId) {
        log.info("Getting from user with ID {}, for comment with ID {}", userId, commentId);
        checkUserExists(userId);
        return commentMapper.mapToCommentDto(commentRepository.findByIdAndAuthorIdAndIsDeletedFalse(
                commentId, userId).orElseThrow(
                () -> new EntityNotFoundException("Comment not found with ID: " + commentId)));
    }

    @Override
    @Transactional
    public CommentDto addNewComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info("Adding new comment of user with ID {}", userId);
        User user = createUserById(userId);
        Event event = createEventById(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            log.warn("Event with ID {} is not published. Cannot add new comment", eventId);
            throw new ValidationException("Event is not published. Cannot add new comment");
        }
        Comment comment = commentMapper.mapToComment(newCommentDto, user, event);
        comment = commentRepository.save(comment);
        log.info("Comment with ID {} has been added successfully", comment.getId());
        return commentMapper.mapToCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateCommentByUser(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        log.info("Updating comment from user with ID {}, for comment with ID {}", userId, commentId);
        User user = createUserById(userId);
        Comment comment = createCommentById(commentId);
        checkCommentByAuthor(user, comment);
        comment.setText(updateCommentDto.getText());
        comment.setUpdated(LocalDateTime.now());
        commentRepository.save(comment);
        log.info("Comment with ID {} has been updated successfully", comment.getId());
        return commentMapper.mapToCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteCommentById(Long userId, Long commentId) {
        log.info("Deleting comment from user with ID {}, for comment with ID {}", userId, commentId);
        User user = createUserById(userId);
        Comment comment = createCommentById(commentId);
        checkCommentByAuthor(user, comment);
        comment.setIsDeleted(true);
        comment.setStatus(StatusComment.DELETED);
        commentRepository.save(comment);
        log.info("Comment with ID {} has been soft-deleted by User with ID {} successfully", commentId, userId);
    }

    /**
     * Добавляет ответ на комментарий.
     */
    @Override
    @Transactional
    public CommentDto addNewReply(Long userId, Long eventId, Long parentCommentId, NewCommentDto newCommentDto) {
        log.info("User {} adding reply to comment {} in event {}. Text: {}",
                userId, parentCommentId, eventId,
                newCommentDto.getText().substring(0, Math.min(20, newCommentDto.getText().length())) + "...");
        User user = createUserById(userId);
        Event event = createEventById(eventId);
        Comment parentComment = createCommentById(parentCommentId);
        if (parentComment.getIsDeleted()) {
            log.warn("Parent comment with ID {} is deleted. Cannot add new reply", parentCommentId);
            throw new ValidationException("Parent comment is deleted. Cannot add new reply");
        }
        if (!parentComment.getEvent().getId().equals(eventId)) {
            log.warn("Parent comment with ID {} is not in the same event", parentCommentId);
            throw new ValidationException("Parent comment is not in the same event");
        }
        Comment commentReply = commentMapper.mapToComment(newCommentDto, user, event);
        commentReply.setParentComment(parentComment);
        commentReply = commentRepository.save(commentReply);
        parentComment.getReplies().add(commentReply);
        commentRepository.save(parentComment);
        log.info("New reply comment with ID {} has been added successfully", commentReply.getId());
        return commentMapper.mapToCommentDto(commentReply);
    }

    @Override
    public String getUserNameById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId))
                .getName();
    }

    private Event createEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Event not found with ID: " + eventId));
    }

    private User createUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found with ID: " + userId));
    }

    private Comment createCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Comment not found with ID: " + commentId));
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with ID: " + userId + " not found");
        }
    }

    private void checkCommentByAuthor(User user, Comment comment) {
        if (!comment.getAuthor().equals(user)) {
            log.warn("User with ID {} is not the author of the comment with ID {}", user.getId(), comment.getId());
            throw new SelfParticipationException("User is not the author of the comment");
        }
    }

    private List<Comment> findAllReplyComments(Comment parentComment, StatusComment status) {
        List<Comment> allReplies = new ArrayList<>();
        Queue<Comment> queue = new LinkedList<>(parentComment.getReplies());
        while (!queue.isEmpty()) {
            Comment reply = queue.poll();
            if (status == null || reply.getStatus() == status) {
                allReplies.add(reply);
                queue.addAll(reply.getReplies());
            }
        }
        return allReplies;
    }
}
