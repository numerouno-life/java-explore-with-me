package ru.practicum.service.compilation;

import ru.practicum.dtos.compilation.CompilationDto;
import ru.practicum.dtos.compilation.NewCompilationDto;
import ru.practicum.dtos.request.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compilationId);

    CompilationDto addNewCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest newCompilationDto);
}
