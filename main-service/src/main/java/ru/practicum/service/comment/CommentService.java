package ru.practicum.service.comment;

import ru.practicum.dtos.comment.CommentDto;
import ru.practicum.dtos.comment.NewCommentDto;
import ru.practicum.dtos.comment.UpdateCommentDto;
import ru.practicum.enums.StatusComment;

import java.util.List;

public interface CommentService {

    List<CommentDto> getAllCommentsByText(String text, Integer from, Integer size);

    List<CommentDto> getCommentsForModeration(Integer from, Integer size);

    CommentDto updateCommentStatusByAdmin(Long commentId, StatusComment status);

    void deleteCommentByIdFromAdmin(Long commentId);

    List<CommentDto> getCommentsAboutEvent(Long eventId);

    List<CommentDto> getAllCommentsAboutUser(Long userId);

    List<CommentDto> getInfoAboutCommentById(Long commentId);

    CommentDto addNewComment(Long userId, NewCommentDto newCommentDto);

    CommentDto updateCommentById(Long userId, Long eventId, UpdateCommentDto updateCommentDto);

    void deleteCommentById(Long userId, Long eventId);

    CommentDto addNewReply(Long eventId, Long parentCommentId, CommentDto commentDto);

    String getUserNameById(Long userId);
}
