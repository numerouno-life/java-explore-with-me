package ru.practicum.dtos.event;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    List<Long> requestIds = new ArrayList<>();
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    UpdateStatus status;
}
