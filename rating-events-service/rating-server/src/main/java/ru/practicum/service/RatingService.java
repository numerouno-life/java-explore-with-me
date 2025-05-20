package ru.practicum.service;

import ru.practicum.RatingResponseDto;

public interface RatingService {

    void likeEvent(Long eventId, Long userId);

    void dislikeEvent(Long eventId, Long userId);

    RatingResponseDto totalRating(Long eventId);
}
