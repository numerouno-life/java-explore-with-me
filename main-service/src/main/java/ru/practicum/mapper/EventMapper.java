package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dtos.category.CategoryDto;
import ru.practicum.dtos.event.*;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public Event toEntity(NewEventDto newEventDtoDto, Location location, Category category) {
        return Event.builder()
                .eventDate(newEventDtoDto.getEventDate())
                .annotation(newEventDtoDto.getAnnotation())
                .category(category)
                .location(location)
                .eventDate(newEventDtoDto.getEventDate())
                .paid(newEventDtoDto.isPaid())
                .participantLimit(newEventDtoDto.getParticipantLimit())
                .requestModeration(newEventDtoDto.isRequestModeration())
                .title(newEventDtoDto.getTitle())
                .description(newEventDtoDto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public EventFullDto mapToEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory() == null ? new CategoryDto() : CategoryMapper.toDto(event.getCategory()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .title(event.getTitle())
                .description(event.getDescription())
                .build();
    }

    public EventShortDto mapToEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category((event.getCategory() == null) ? new CategoryDto() : CategoryMapper.toDto(event.getCategory()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedRequest())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .views(event.getViews())
                .build();
    }


    public UpdateEventAdminRequest mapToUpdateEventAdminRequest(UpdateEventUserRequest updateEventUserRequest) {
        return UpdateEventAdminRequest.builder()
                .annotation(updateEventUserRequest.getAnnotation())
                .category(updateEventUserRequest.getCategory())
                .description(updateEventUserRequest.getDescription())
                .eventDate(updateEventUserRequest.getEventDate())
                .paid(updateEventUserRequest.isPaid())
                .participantLimit(updateEventUserRequest.getParticipantLimit())
                .title(updateEventUserRequest.getTitle())
                .build();
    }
}
