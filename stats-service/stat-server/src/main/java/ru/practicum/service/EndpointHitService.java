package ru.practicum.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;

import java.util.List;

public interface EndpointHitService {

    void saveStat(EndpointHitDto statDto);

    List<ViewStats> getStat(String start, String end, List<String> uris, boolean unique);
}
