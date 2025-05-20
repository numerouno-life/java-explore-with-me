package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.DislikeRequestDto;
import ru.practicum.LikeRequestDto;
import ru.practicum.RatingResponseDto;
import ru.practicum.mapper.RatingServerMapper;
import ru.practicum.model.EventRating;
import ru.practicum.repository.EventRatingRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final EventRatingRepository eventRatingRepository;
    private final RatingServerMapper ratingServerMapper;

    @Transactional
    @Override
    public void likeEvent(Long eventId, Long userId) {
        log.info("Processing like: eventId={}, userId={}", eventId, userId);

        if (eventRatingRepository.existsByEventIdAndUserIdAndType(eventId, userId, "LIKE")) {
            eventRatingRepository.deleteByEventIdAndUserIdAndType(eventId, userId, "LIKE");
            log.info("Like was removed from event with eventId={}, userId={}", eventId, userId);
        } else {
            if (eventRatingRepository.existsByEventIdAndUserIdAndType(eventId, userId, "DISLIKE")) {
                eventRatingRepository.deleteByEventIdAndUserIdAndType(eventId, userId, "DISLIKE");
                log.info("Dislike was removed from event with eventId={}, userId={}", eventId, userId);
            }
            EventRating like = ratingServerMapper.toLikeDto(new LikeRequestDto(eventId, userId));
            log.info("Saving rating: eventId={}, userId={}, type={}", eventId, userId, like.getType());
            eventRatingRepository.save(like);
            log.info("Like was added to event with eventId={}, userId={}", eventId, userId);
        }
    }

    @Transactional
    @Override
    public void dislikeEvent(Long eventId, Long userId) {
        log.info("Add dislike to event with eventId={}, userId={}", eventId, userId);
        if (eventRatingRepository.existsByEventIdAndUserIdAndType(eventId, userId, "DISLIKE")) {
            eventRatingRepository.deleteByEventIdAndUserIdAndType(eventId, userId, "DISLIKE");
            log.info("Dislike was removed from event with eventId={}, userId={}", eventId, userId);
        } else {
            if (eventRatingRepository.existsByEventIdAndUserIdAndType(eventId, userId, "LIKE")) {
                eventRatingRepository.deleteByEventIdAndUserIdAndType(eventId, userId, "LIKE");
                log.info("Delete like from event with eventId={}, userId={} ," +
                        " because user dislike this event", eventId, userId);
            }
            EventRating dislike = ratingServerMapper.toDislikeDto(new DislikeRequestDto(eventId, userId));
            eventRatingRepository.save(dislike);
            log.info("Dislike was added to event with eventId={}, userId={}", eventId, userId);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public RatingResponseDto totalRating(Long eventId) {
        log.info("Get total rating for event with eventId={}", eventId);
        Long like = eventRatingRepository.countByEventIdAndType(eventId, "LIKE");
        Long dislike = eventRatingRepository.countByEventIdAndType(eventId, "DISLIKE");

        return RatingResponseDto.builder()
                .likes(like)
                .dislikes(dislike)
                .totalRating(like - dislike)
                .build();
    }

}
