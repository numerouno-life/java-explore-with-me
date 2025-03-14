package ru.practicum.dtos.event;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.UpdateStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {

    final List<Long> requestIds = new ArrayList<>();
    @NotNull
    UpdateStatus status;
}
