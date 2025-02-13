package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dtos.compilation.CompilationDto;
import ru.practicum.model.Compilation;

@Mapper
public interface CompilationMapper {

    CompilationDto toDto(Compilation compilation);

    Compilation toEntity(CompilationDto compilationDto);

}
