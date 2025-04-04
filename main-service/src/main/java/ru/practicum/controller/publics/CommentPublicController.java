package ru.practicum.controller.publics;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.comment.CommentDto;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentPublicController {

    private CommentService commentService;

    @GetMapping("/{eventId}")
    public List<CommentDto> getCommentsAboutEvent(@PathVariable @PositiveOrZero Long eventId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /comments/users/{}", eventId);
        return commentService.getCommentsAboutEvent(eventId, from, size);
    }
}
