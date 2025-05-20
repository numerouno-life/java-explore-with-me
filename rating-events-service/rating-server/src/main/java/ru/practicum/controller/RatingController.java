package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.RatingResponseDto;
import ru.practicum.service.RatingService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/{eventId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void likeEvent(@PathVariable Long eventId, @RequestParam Long userId) {
        log.info("Received like request: eventId={}, userId={}", eventId, userId);
        ratingService.likeEvent(eventId, userId);
    }

    @PostMapping("/{eventId}/dislike")
    @ResponseStatus(HttpStatus.CREATED)
    public void dislikeEvent(@PathVariable Long eventId, @RequestParam Long userId) {
        ratingService.dislikeEvent(eventId, userId);
    }

    @GetMapping("/{eventId}/rating")
    public RatingResponseDto totalRating(@PathVariable Long eventId) {
        return ratingService.totalRating(eventId);
    }
}
