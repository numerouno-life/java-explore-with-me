package ru.practicum.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    EndpointHit toEntity(EndpointHitDto dto);
}

