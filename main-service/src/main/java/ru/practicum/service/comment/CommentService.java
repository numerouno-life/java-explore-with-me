package ru.practicum.service.comment;

import ru.practicum.dtos.comment.CommentDto;
import ru.practicum.dtos.comment.NewCommentDto;
import ru.practicum.dtos.comment.StatusUpdateDto;
import ru.practicum.dtos.comment.UpdateCommentDto;
import ru.practicum.enums.StatusComment;

import java.util.List;

public interface CommentService {

    List<CommentDto> getAllCommentsByFilter(String text, StatusComment status, Integer from, Integer size);

    CommentDto getCommentByIdFromAdmin(Long commentId);

    CommentDto updateCommentStatusByAdmin(Long commentId, StatusUpdateDto status);

    void deleteCommentByIdFromAdmin(Long commentId);

    void hardDeleteComment(Long commentId);

    List<CommentDto> getCommentsAboutEvent(Long eventId, Integer from, Integer size);

    List<CommentDto> getUserAuthoredComments(Long userId);

    CommentDto getCommentById(Long userId, Long commentId);

    CommentDto addNewComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateCommentByUser(Long userId, Long commentId, UpdateCommentDto updateCommentDto);

    void deleteCommentById(Long userId, Long commentId);

    CommentDto addNewReply(Long userId, Long eventId, Long parentCommentId, NewCommentDto newCommentDto);

    String getUserNameById(Long userId);
}
