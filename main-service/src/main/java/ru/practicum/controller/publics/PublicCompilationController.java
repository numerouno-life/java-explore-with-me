package ru.practicum.controller.publics;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.compilation.CompilationDto;
import ru.practicum.service.compilation.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                   Integer from,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return compilationService.getAllCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public List<CompilationDto> getCompilationsById(@PathVariable Long compId) {
        return compilationService.getEventsById(compId);
    }
}
