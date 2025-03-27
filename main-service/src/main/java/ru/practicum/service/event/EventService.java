package ru.practicum.service.event;

import ru.practicum.dtos.event.*;
import ru.practicum.dtos.request.ParticipationRequestDto;
import ru.practicum.enums.State;

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

    List<EventFullDto> getAdminEvents(List<Long> users, List<State> states, List<Long> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEventOfAdmin(Long eventId, UpdateEventAdminRequest eventAdminRequest);

    List<EventShortDto> getShortEventPublicByFilter(String text,
                                                    List<Long> categories,
                                                    Boolean paid,
                                                    LocalDateTime rangeStart,
                                                    LocalDateTime rangeEnd,
                                                    boolean onlyAvailable,
                                                    String sort,
                                                    int from,
                                                    int size);

    EventFullDto getPublicEventById(Long id);
}
