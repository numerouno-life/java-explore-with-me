package ru.practicum.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.practicum.dtos.event.*;
import ru.practicum.dtos.request.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventFullDto> getAllEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto addNewEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventOfUser(Long userId, Long eventId);

    EventFullDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest eventUserRequest);

    List<ParticipationRequestDto> getRequestsOfUserEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatusOfUserEvent(Long userId, Long eventId,
                                                 EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> searchEvents(List<Long> users, List<String> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest eventAdminRequest);

    List<EventFullDto> getAllEventPublic(SearchEventParamPublic searchEventParamPublic);

    EventFullDto getEventPrivate(Long userId, Long eventId);

    EventFullDto getEvent(Long id) throws JsonProcessingException;
}
