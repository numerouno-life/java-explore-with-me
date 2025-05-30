package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dtos.compilation.CompilationDto;
import ru.practicum.dtos.compilation.NewCompilationDto;
import ru.practicum.model.Compilation;

import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream()
                        .map(EventMapper::mapToShortDto)
                        .collect(Collectors.toSet()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

}
