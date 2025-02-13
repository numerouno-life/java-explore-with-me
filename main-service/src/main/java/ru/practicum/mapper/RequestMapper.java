package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dtos.request.ParticipationRequestDto;
import ru.practicum.model.Request;

import java.util.List;

@Mapper
public interface RequestMapper {

    ParticipationRequestDto toParticipationRequestDto(Request request);

    List<ParticipationRequestDto> toParticipationRequestDtoList(List<Request> requests);

    Request toEntity(ParticipationRequestDto participationRequestDto);
}
