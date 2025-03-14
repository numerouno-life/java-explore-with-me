package ru.practicum.controller.publics;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatClient;
import ru.practicum.dto.StatDto;
import ru.practicum.dtos.event.EventFullDto;
import ru.practicum.dtos.event.SearchEventParamPublic;
import ru.practicum.service.event.EventService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventsPublicController {
    private final EventService eventService;
    private final StatClient statClient;

    @Value("${server.application.name:ewm-service}")
    private String applicationName;

    @GetMapping
    public List<EventFullDto> searchEvents(@Valid @ModelAttribute SearchEventParamPublic searchEventParamPublic, HttpServletRequest request) {
        List<EventFullDto> events = eventService.getAllEventPublic(searchEventParamPublic);

        StatDto statDto = new StatDto(applicationName, null, new HashSet<>(), request.getRemoteAddr(), LocalDateTime.now());

        for (EventFullDto event : events) {
            statDto.getUris().add("/events/" + event.getId());
        }

        statClient.addStatEvent(statDto);

        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getPrivateEvent(@PathVariable @Positive Long id,
                                        HttpServletRequest request) throws JsonProcessingException {

        EventFullDto event = eventService.getEvent(id);

        statClient.addStatEvent(new StatDto(applicationName, "/events/" + id, null, request.getRemoteAddr(), LocalDateTime.now()));

        return event;
    }
}
