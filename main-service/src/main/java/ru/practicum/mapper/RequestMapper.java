package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dtos.request.ParticipationRequestDto;
import ru.practicum.model.Request;

import java.util.List;

@UtilityClass
public class RequestMapper {

    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }

    public List<ParticipationRequestDto> toParticipationRequestDto(List<Request> requests) {
        return requests.stream().map(RequestMapper::toParticipationRequestDto).toList();
    }
}
