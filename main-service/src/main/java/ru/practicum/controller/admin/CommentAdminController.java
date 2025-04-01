package ru.practicum.controller.admin;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.comment.CommentDto;
import ru.practicum.enums.StatusComment;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class CommentAdminController {

    private CommentService commentService;

    @GetMapping("/search")
    public List<CommentDto> getAllCommentsBySearch(@RequestParam String search,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /admin/comments/search");
        return commentService.getAllCommentsByText(search, from, size);
    }

    @GetMapping("/checking")
    public List<CommentDto> getCommentsForModeration(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /admin/comments/checking");
        return commentService.getCommentsForModeration(from, size);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateCommentStatusByAdmin(@PathVariable @PositiveOrZero Long commentId,
                                                 @RequestParam(required = false) StatusComment status) {
        log.info("PATCH /admin/comments/{}", commentId);
        return commentService.updateCommentStatusByAdmin(commentId, status);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByIdFromAdmin(@PathVariable @PositiveOrZero Long commentId) {
        log.info("DELETE /admin/comments/{}", commentId);
        commentService.deleteCommentByIdFromAdmin(commentId);
    }

}
