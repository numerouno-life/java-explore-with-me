package ru.practicum.controller.privates;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.RatingClient;
import ru.practicum.RatingResponseDto;
import ru.practicum.dtos.event.*;
import ru.practicum.dtos.request.ParticipationRequestDto;
import ru.practicum.service.event.EventService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventsPrivateController {

    private final EventService eventsService;
    private final RatingClient ratingClient;

    @GetMapping
    public List<EventFullDto> getAllEventsByUserId(
            @PositiveOrZero @PathVariable Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return eventsService.getAllEventsByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addNewEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        return eventsService.addNewEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventOfUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventsService.getEventOfUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventOfUser(@Valid @RequestBody UpdateEventUserRequest updateRequest,
                                          @PathVariable Long userId,
                                          @PathVariable Long eventId) {
        return eventsService.updateEventOfUser(userId, eventId, updateRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsOfUserEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventsService.getRequestsOfUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatusOfUserEvent(
            @PathVariable Long userId, @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Received update request: userId={}, eventId={}, body={}",
                userId, eventId, eventRequestStatusUpdateRequest);
        return eventsService.updateRequestsStatusOfUserEvent(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @PostMapping("/{eventId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> likeEvent(@PathVariable Long eventId, @PathVariable Long userId) {
        log.info("Received like request: userId={}, eventId={}", userId, eventId);
        eventsService.validateUserAndEvent(userId, eventId);
        ratingClient.likeEvent(eventId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{eventId}/dislike")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> dislikeEvent(@PathVariable Long eventId, @PathVariable Long userId) {
        log.info("Received dislike request: userId={}, eventId={}", userId, eventId);
        eventsService.validateUserAndEvent(userId, eventId);
        ratingClient.dislikeEvent(eventId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{eventId}/rating")
    public ResponseEntity<RatingResponseDto> totalRating(@PathVariable Long eventId) {
        log.info("Received totalRating request: eventId={}", eventId);
        RatingResponseDto response = ratingClient.totalRating(eventId);
        return ResponseEntity.ok(response);
    }
}
