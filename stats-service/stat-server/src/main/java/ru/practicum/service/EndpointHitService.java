package ru.practicum.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;

import java.util.List;

public interface EndpointHitService {

    void saveStat(EndpointHitDto statDto);

    List<ViewStats> getViewStats(String start, String end, List<String> uris, boolean unique);
}
