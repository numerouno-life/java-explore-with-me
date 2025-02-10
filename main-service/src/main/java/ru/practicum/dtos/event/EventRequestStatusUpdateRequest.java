package ru.practicum.dtos.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {

    final List<Integer> requestIds = new ArrayList<>();
    UpdateStatus status;

    public enum UpdateStatus {
        CONFIRMED, REJECTED
    }
}
