package ru.practicum.service.compilation;

import ru.practicum.dtos.compilation.CompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);

    List<CompilationDto> getEventsById(Long compilationId);
}
