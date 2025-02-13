package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dtos.event.*;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.util.List;

@Mapper
public interface EventMapper {

    Event mapToEvent(NewEventDto newEventDtoDto, Category category, User initiator);

    EventFullDto mapToEventFullDto(Event event);

    EventShortDto mapToEventShortDto(Event event);

    List<EventFullDto> mapToEventFullDtoList(List<Event> events);

    List<EventShortDto> mapToEventShortDtoList(List<Event> events);

    UpdateEventAdminRequest mapToUpdateEventAdminRequest(UpdateEventUserRequest updateEventUserRequest);
}
