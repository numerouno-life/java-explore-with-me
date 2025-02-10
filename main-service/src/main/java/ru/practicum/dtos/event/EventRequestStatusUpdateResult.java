package ru.practicum.dtos.event;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dtos.request.ParticipationRequestDto;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateResult {

    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;
}
