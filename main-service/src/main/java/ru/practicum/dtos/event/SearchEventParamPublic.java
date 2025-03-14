package ru.practicum.dtos.event;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.enums.SortType;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.dtos.utils.DateTimeFormatter.FORMAT;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchEventParamPublic {

    private String text;

    private List<Long> categories;

    private Boolean paid;

    @DateTimeFormat(pattern = FORMAT)
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = FORMAT)
    private LocalDateTime rangeEnd;

    private Boolean onlyAvailable = false;

    private SortType sort = SortType.EVENT_DATE;

    @PositiveOrZero
    private Integer from = 0;

    @Positive
    private Integer size = 10;
}