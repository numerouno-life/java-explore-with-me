package ru.practicum.dtos.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import ru.practicum.enums.StatusComment;

@Getter
public class StatusUpdateDto {
    @NotNull
    @JsonProperty("status")
    private StatusComment status;
}
