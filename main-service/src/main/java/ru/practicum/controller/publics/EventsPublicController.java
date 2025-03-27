package ru.practicum.controller.publics;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatClient;
import ru.practicum.dtos.event.EventFullDto;
import ru.practicum.dtos.event.EventShortDto;
import ru.practicum.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventsPublicController {
    private final EventService eventService;
    private final StatClient statClient;

    @Value("${stats-service.url}")
    private String applicationName;

    @GetMapping
    public List<EventShortDto> getShortEventPublicByFilter(
                                                        @RequestParam(required = false) String text,
                                                        @RequestParam(required = false) List<Long> categories,
                                                        @RequestParam(required = false) Boolean paid,
                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = FORMAT)
                                                        LocalDateTime rangeStart,
                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = FORMAT)
                                                        LocalDateTime rangeEnd,
                                                        @RequestParam(defaultValue = "false")
                                                        boolean onlyAvailable,
                                                        @RequestParam(required = false) String sort,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        HttpServletRequest request) {
        statClient.save(applicationName, request);
        log.info("GET request /events");
        return eventService.getShortEventPublicByFilter(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getPublicEventById(@PathVariable @Positive Long id,
                                           HttpServletRequest request) throws JsonProcessingException {
        log.info("GET /events/{}", id);
        statClient.save(applicationName, request);
        return eventService.getPublicEventById(id);
    }
}
