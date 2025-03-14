package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.event.EventFullDto;
import ru.practicum.dtos.event.UpdateEventAdminRequest;
import ru.practicum.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventsAdminController {

    private final EventService eventsService;

    @GetMapping
    public List<EventFullDto> searchEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = FORMAT) LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return null;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PositiveOrZero @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest eventAdminRequest) {
        return null;
    }

}
