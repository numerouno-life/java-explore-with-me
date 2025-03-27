package ru.practicum.service.UserRequest;

import ru.practicum.dtos.request.ParticipationRequestDto;

import java.util.List;

public interface UserRequestService {

    List<ParticipationRequestDto> getAllUserRequests(Long userId);

    ParticipationRequestDto createUserRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelUserRequest(Long userId, Long requestId);
}
