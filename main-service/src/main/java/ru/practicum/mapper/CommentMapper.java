package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dtos.comment.CommentDto;
import ru.practicum.dtos.comment.NewCommentDto;
import ru.practicum.model.Comment;
import ru.practicum.service.comment.CommentService;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorName", source = "userId", qualifiedByName = "getUserNameById")
    CommentDto mapToDto(Comment comment);

    Comment mapToComment(NewCommentDto newCommentDto);

    default String getUserNameById(Long userId, CommentService commentService) {
        return commentService.getUserNameById(userId);
    }
}
