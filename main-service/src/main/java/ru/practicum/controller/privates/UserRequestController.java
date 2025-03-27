package ru.practicum.controller.privates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.request.ParticipationRequestDto;
import ru.practicum.service.UserRequest.UserRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class UserRequestController {
    private final UserRequestService userRequestService;

    @GetMapping
    public List<ParticipationRequestDto> getAllUserRequests(@PathVariable Long userId) {
        log.info("GET /users/{userId}/requests");
        return userRequestService.getAllUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createUserRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("POST /users/{userId}/requests");
        return userRequestService.createUserRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelUserRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("PATCH /users/{userId}/requests/{requestId}/cancel");
        return userRequestService.cancelUserRequest(userId, requestId);
    }
}
