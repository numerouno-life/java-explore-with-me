package ru.practicum.service.comment;

import ru.practicum.dtos.comment.CommentDto;
import ru.practicum.dtos.comment.NewCommentDto;
import ru.practicum.dtos.comment.UpdateCommentDto;
import ru.practicum.enums.StatusComment;

import java.util.List;

public interface CommentService {

    List<CommentDto> getAllCommentsByText(String text, StatusComment status, Integer from, Integer size);

    List<CommentDto> getCommentsForModeration(Integer from, Integer size);

    CommentDto updateCommentStatusByAdmin(Long commentId, StatusComment status);

    void deleteCommentByIdFromAdmin(Long commentId);

    void hardDeleteComment(Long commentId);

    List<CommentDto> getCommentsAboutEvent(Long eventId, Integer from, Integer size);

    List<CommentDto> getUserAuthoredComments(Long userId);

    CommentDto getCommentById(Long userId, Long commentId);

    CommentDto addNewComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateCommentByUser(Long userId, Long commentId, UpdateCommentDto updateCommentDto);

    void deleteCommentById(Long userId, Long commentId);

    CommentDto addNewReply(Long eventId, Long parentCommentId, CommentDto commentDto);

    String getUserNameById(Long userId);
}
