package ru.practicum.controller.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.compilation.CompilationDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationController {

    @GetMapping
    public List<CompilationDto> getEvents(@RequestParam(required = false) boolean pinned,
                                          @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return null;
    }

    @GetMapping("/{compId}")
    public List<CompilationDto> getEventsById(@PathVariable Integer compId) {
        return null;
    }
}
