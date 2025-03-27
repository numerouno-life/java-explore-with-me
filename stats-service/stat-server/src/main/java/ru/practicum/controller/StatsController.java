package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ViewStats;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.service.EndpointHitService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final EndpointHitService endpointHitService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        endpointHitService.saveStat(endpointHitDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> getViewStats(@RequestParam String start,
                                                 @RequestParam String end,
                                                 @RequestParam(required = false) List<String> uris,
                                                 @RequestParam(required = false) boolean unique) {
        return endpointHitService.getViewStats(start, end, uris, unique);
    }
}
