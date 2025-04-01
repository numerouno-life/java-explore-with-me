package ru.practicum.controller.publics;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public List<CommentDto> getCommentsAboutEvent(@PathVariable @PositiveOrZero Long eventId) {
        log.info("GET /comments/users/{}", eventId);
        return commentService.getCommentsAboutEvent(eventId);
    }
}
