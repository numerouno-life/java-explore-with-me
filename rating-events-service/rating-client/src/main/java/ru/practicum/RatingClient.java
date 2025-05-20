package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class RatingClient {

    private final RestClient client;

    public RatingClient(@Value("${rating.service.url}") String serverUrl) {
        this.client = RestClient.create(serverUrl);
        log.info("Rating-service run URL: {}", serverUrl);
    }

    public void likeEvent(Long eventId, Long userId) {
        LikeRequestDto request = new LikeRequestDto(eventId, userId);
        client.post()
                .uri("/events/{eventId}/like", eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void unlikeEvent(Long eventId, Long userId) {
        client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/events/{eventId}/like")
                        .queryParam("userId", userId)
                        .build(eventId))
                .retrieve()
                .toBodilessEntity();
    }

    public void dislikeEvent(Long eventId, Long userId) {
        DislikeRequestDto request = new DislikeRequestDto(eventId, userId);
        client.post()
                .uri("/events/{eventId}/dislike", eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void undislikeEvent(Long eventId, Long userId) {
        client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/events/{eventId}/dislike")
                        .queryParam("userId", userId)
                        .build(eventId))
                .retrieve()
                .toBodilessEntity();
    }

    public RatingResponseDto totalRating(Long eventId) {
        return client.get()
                .uri("/events/{eventId}/rating", eventId)
                .retrieve()
                .body(RatingResponseDto.class);
    }
}