package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dtos.compilation.CompilationDto;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.repository.CompilationRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;


    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        return List.of();
    }

    @Override
    public List<CompilationDto> getEventsById(Long compilationId) {
        return List.of();
    }
}
