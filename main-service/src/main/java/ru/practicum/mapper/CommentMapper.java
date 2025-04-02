package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.dtos.comment.CommentDto;
import ru.practicum.dtos.comment.NewCommentDto;
import ru.practicum.model.Comment;
import ru.practicum.service.comment.CommentService;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorName", expression = "java(commentService.getUserNameById(comment.getUserId()))")
    CommentDto mapToDto(Comment comment, CommentService commentService);

    Comment mapToComment(NewCommentDto newCommentDto);

    @Named("getUserNameById")
    default String getUserNameById(Long userId, CommentService commentService) {
        return commentService.getUserNameById(userId);
    }
}
