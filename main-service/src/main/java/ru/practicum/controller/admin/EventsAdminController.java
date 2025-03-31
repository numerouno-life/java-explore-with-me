package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.event.EventFullDto;
import ru.practicum.dtos.event.UpdateEventAdminRequest;
import ru.practicum.enums.State;
import ru.practicum.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventsAdminController {

    private final EventService eventsService;

    @GetMapping
    public List<EventFullDto> getAdminEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<State> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = FORMAT) LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return eventsService.getAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventOfAdmin(@PositiveOrZero @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest eventAdminRequest) {
        log.info("/admin/events/PATCH/updateEventOfAdmin");
        return eventsService.updateEventOfAdmin(eventId, eventAdminRequest);
    }

}
