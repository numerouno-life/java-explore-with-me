package ru.practicum.controller.privates;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.comment.CommentDto;
import ru.practicum.dtos.comment.NewCommentDto;
import ru.practicum.dtos.comment.UpdateCommentDto;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments/users/{userId}")
public class CommentPrivateController {

    private final CommentService commentService;

    @GetMapping("/comments")
    public List<CommentDto> getUserAuthoredComments(@PathVariable @PositiveOrZero Long userId) {
        log.info("GET /comments/users/{}/comments", userId);
        return commentService.getUserAuthoredComments(userId);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable @PositiveOrZero Long userId,
                                     @PathVariable @PositiveOrZero Long commentId) {
        log.info("GET /comments/users/{}/comments/{}", userId, commentId);
        return commentService.getCommentById(userId, commentId);
    }

    @PostMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addNewComment(@PathVariable @PositiveOrZero Long userId,
                                    @PathVariable @PositiveOrZero Long eventId,
                                    @RequestBody @Validated NewCommentDto newCommentDto) {
        log.info("POST /comments/users/{}/events", userId);
        return commentService.addNewComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateCommentByUser(@PathVariable @PositiveOrZero Long userId,
                                          @PathVariable @PositiveOrZero Long commentId,
                                          @RequestBody @Validated UpdateCommentDto updateCommentDto) {
        log.info("PATCH /comments/users/{}/comments/{}", userId, commentId);
        return commentService.updateCommentByUser(userId, commentId, updateCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById(@PathVariable @PositiveOrZero Long userId,
                                  @PathVariable @PositiveOrZero Long commentId) {
        log.info("DELETE /comments/users/{}/comments/{}", userId, commentId);
        commentService.deleteCommentById(userId, commentId);
    }

    @PostMapping("/events/{eventId}/comments/{parentCommentId}/replies")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addNewReply(@PathVariable @PositiveOrZero Long eventId,
                                  @PathVariable @PositiveOrZero Long parentCommentId,
                                  @RequestBody @Valid CommentDto commentDto) {
        log.info("POST /admin/comments/events/{}/comments/{}/replies", eventId, parentCommentId);
        return commentService.addNewReply(eventId, parentCommentId, commentDto);
    }
}