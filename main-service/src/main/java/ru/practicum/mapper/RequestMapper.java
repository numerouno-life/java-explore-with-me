package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dtos.request.ParticipationRequestDto;
import ru.practicum.model.Request;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class RequestMapper {

    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(LocalDateTime.now())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

    public Request toEntity(ParticipationRequestDto participationRequestDto) {
        return Request.builder()
                .id(participationRequestDto.getId())
                .created(participationRequestDto.getCreated())
                .status(participationRequestDto.getStatus())
                .build();
    }

    public List<ParticipationRequestDto> toParticipationRequestDtoList(List<Request> requests) {
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }
}
