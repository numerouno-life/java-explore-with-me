package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dtos.compilation.CompilationDto;
import ru.practicum.dtos.compilation.NewCompilationDto;
import ru.practicum.dtos.request.UpdateCompilationRequest;
import ru.practicum.error.exception.EntityNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("Get all compilations with pinned={}, from={}, size={}", pinned, from, size);
        Pageable pageable = PageRequest.of(from / size, size, Sort.unsorted());
        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, pageable).stream()
                    .map(CompilationMapper::toCompilationDto)
                    .toList();
        } else {
            return compilationRepository.findAll(pageable).stream()
                    .map(CompilationMapper::toCompilationDto)
                    .toList();
        }
    }

    @Override
    public CompilationDto getCompilationById(Long compilationId) {
        log.info("Get events by compilation id={}", compilationId);
        return CompilationMapper.toCompilationDto(findCompilationById(compilationId));
    }

    @Transactional
    @Override
    public CompilationDto addNewCompilation(NewCompilationDto newCompilationDto) {
        log.info("Add new compilation={}", newCompilationDto);
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        compilation.setPinned(Optional.ofNullable(newCompilationDto.getPinned()).orElse(false));
        Set<Long> compEventsId = (newCompilationDto.getEvents() != null) ? newCompilationDto.getEvents() : Collections.emptySet();
        Set<Event> events = eventRepository.findAllByIdIn(compEventsId);
        compilation.setEvents(events);
        Compilation result = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(result);
    }

    @Transactional
    @Override
    public void deleteCompilationById(Long compId) {
        log.info("Delete compilation by id={}", compId);
        findCompilationById(compId);
        compilationRepository.delete(findCompilationById(compId));
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Update compilation with id={} and new data={}", compId, updateCompilationRequest);
        log.info("Received update request: {}", updateCompilationRequest);

        Compilation compilation = findCompilationById(compId);
        Set<Long> eventsId = updateCompilationRequest.getEvents();
        if (eventsId != null && !eventsId.isEmpty()) {
            log.info("Fetched events: {}", eventRepository.findAllByIdIn(eventsId));
            compilation.setEvents(eventRepository.findAllByIdIn(eventsId));
        }
        Optional.ofNullable(updateCompilationRequest.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(updateCompilationRequest.getPinned()).ifPresent(compilation::setPinned);
        Compilation saved = compilationRepository.save(compilation);
        log.info("Updated compilation: {}", saved);
        return CompilationMapper.toCompilationDto(saved);
    }

    private Compilation findCompilationById(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() ->
                new EntityNotFoundException("No compilation with id=" + compId));
    }
}
