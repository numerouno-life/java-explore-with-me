package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dtos.compilation.CompilationDto;
import ru.practicum.dtos.compilation.NewCompilationDto;
import ru.practicum.dtos.event.EventShortDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.HashSet;
import java.util.List;

@UtilityClass
public class CompilationMapper {

    public Compilation toCompilation(NewCompilationDto compilationDto, List<Event> events) {
        return Compilation.builder()
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle())
                .events(new HashSet<>(events))
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> listEventDto) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(listEventDto)
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();


    }
}
