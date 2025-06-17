package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dtos.category.CategoryDto;
import ru.practicum.dtos.event.EventFullDto;
import ru.practicum.dtos.event.EventShortDto;
import ru.practicum.dtos.event.NewEventDto;
import ru.practicum.enums.State;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {
    public Event mapToEvent(NewEventDto eventDto, Category category, User initiator) {
        return Event.builder()
                .eventDate(eventDto.getEventDate())
                .annotation(eventDto.getAnnotation())
                .paid(eventDto.isPaid())
                .category(category)
                .confirmedRequests(0)
                .createdOn(LocalDateTime.now())
                .description(eventDto.getDescription())
                .state(State.PENDING)
                .title(eventDto.getTitle())
                .lat(eventDto.getLocation().getLat())
                .lon(eventDto.getLocation().getLon())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.isRequestModeration())
                .initiator(initiator)
                .build();
    }

    public EventShortDto mapToShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .publishedOn(event.getPublishedOn())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views((event.getViews() != null) ? event.getViews() : 0)
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .createdOn(event.getCreatedOn())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .confirmedRequests(event.getConfirmedRequests())
                .views((event.getViews() != null) ? event.getViews() : 0)
                .state(event.getState())
                .annotation(event.getAnnotation())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .paid(event.getPaid())
                .category((event.getCategory() == null) ?
                        new CategoryDto() : CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .publishedOn(event.getPublishedOn())
                .location(Location.builder().lat(event.getLat()).lon(event.getLon()).build())
                .build();
    }

}
