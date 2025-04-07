package ru.practicum.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dtos.comment.CommentDto;
import ru.practicum.dtos.comment.NewCommentDto;
import ru.practicum.dtos.comment.UpdateCommentDto;
import ru.practicum.enums.State;
import ru.practicum.enums.StatusComment;
import ru.practicum.error.exception.EntityNotFoundException;
import ru.practicum.error.exception.SelfParticipationException;
import ru.practicum.error.exception.ValidationException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentDto> getAllCommentsByText(String text, StatusComment statusComment,
                                                 Integer from, Integer size) {
        return List.of();
    }

    @Override
    public List<CommentDto> getCommentsForModeration(Integer from, Integer size) {
        return List.of();
    }

    @Transactional
    @Override
    public CommentDto updateCommentStatusByAdmin(Long commentId, StatusComment status) {
        log.info("Updating status of comment with ID {} by admin", commentId);
        Comment comment = createCommentById(commentId);
        if (comment.getStatus().equals(StatusComment.PUBLISHED)) {
            log.warn("Comment with ID {} is not in CHECKING state. Cannot update status", commentId);
            throw new ValidationException("Comment is not in CHECKING state. Cannot update status");
        }
        comment.setStatus(status);
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
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findAllByEventIdAndStatusAndIsDeletedFalse(
                eventId, StatusComment.PUBLISHED, pageRequest);
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
        log.info("Getting comment by ID {}", commentId);
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
        comment.setStatus(StatusComment.CHECKING);
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

    @Override
    public CommentDto addNewReply(Long eventId, Long parentCommentId, CommentDto commentDto) {
        return null;
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
}
