package ru.practicum.controller.admin;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.comment.CommentDto;
import ru.practicum.dtos.comment.StatusUpdateDto;
import ru.practicum.enums.StatusComment;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class CommentAdminController {

    private final CommentService commentService;

    @GetMapping("/filter")
    public List<CommentDto> getAllCommentsBySearch(@RequestParam String search,
                                                   @RequestParam(required = false) StatusComment status,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /admin/comments/filter with filter: search ='{}', status={}, from={}, size={}",
                search, status, from, size);
        return commentService.getAllCommentsByFilter(search, status, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentByIdFromAdmin(@PathVariable @PositiveOrZero Long commentId) {
        log.info("GET /admin/comments/{}", commentId);
        return commentService.getCommentByIdFromAdmin(commentId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateCommentStatusByAdmin(@PathVariable @PositiveOrZero Long commentId,
                                                 @RequestBody StatusUpdateDto status) {
        log.info("PATCH /admin/comments/{}", commentId);
        return commentService.updateCommentStatusByAdmin(commentId, status);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByIdFromAdmin(@PathVariable @PositiveOrZero Long commentId) {
        log.info("DELETE /admin/comments/{}", commentId);
        commentService.deleteCommentByIdFromAdmin(commentId);
    }

    @DeleteMapping("/{commentId}/hard")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDeleteComment(@PathVariable @PositiveOrZero Long commentId) {
        log.info("HARD DELETE /admin/comments/{}", commentId);
        commentService.hardDeleteComment(commentId);
    }

}
