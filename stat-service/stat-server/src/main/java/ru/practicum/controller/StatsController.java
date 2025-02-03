package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatOnVisit;
import ru.practicum.dto.StatsDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody StatsDto statsDto) {
        log.info("save stats: {}", statsDto);

    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatOnVisit> getStatisticsOnVisits(@RequestParam String start,
                                                   @RequestParam String end,
                                                   @RequestParam(defaultValue = "") List<String> uris,
                                                   @RequestParam(required = false) boolean unique) {
        log.info("get statistics on visits: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return null;
    }
}
