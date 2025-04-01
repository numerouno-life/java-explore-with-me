package ru.practicum.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dtos.comment.CommentDto;
import ru.practicum.dtos.comment.NewCommentDto;
import ru.practicum.dtos.comment.UpdateCommentDto;
import ru.practicum.enums.StatusComment;
import ru.practicum.error.exception.EntityNotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentDto> getAllCommentsByText(String text, Integer from, Integer size) {
        return List.of();
    }

    @Override
    public List<CommentDto> getCommentsForModeration(Integer from, Integer size) {
        return List.of();
    }

    @Transactional
    @Override
    public CommentDto updateCommentStatusByAdmin(Long commentId, StatusComment status) {
        return null;
    }

    @Transactional
    @Override
    public void deleteCommentByIdFromAdmin(Long commentId) {

    }

    @Override
    public List<CommentDto> getCommentsAboutEvent(Long eventId) {
        return List.of();
    }

    @Override
    public List<CommentDto> getAllCommentsAboutUser(Long userId) {
        return List.of();
    }

    @Override
    public List<CommentDto> getInfoAboutCommentById(Long commentId) {
        return List.of();
    }

    @Override
    public CommentDto addNewComment(Long userId, NewCommentDto newCommentDto) {
        return null;
    }

    @Override
    public CommentDto updateCommentById(Long userId, Long eventId, UpdateCommentDto updateCommentDto) {
        return null;
    }

    @Override
    public void deleteCommentById(Long userId, Long eventId) {

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
}
