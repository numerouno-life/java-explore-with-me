package ru.practicum.service.event;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatClient;
import ru.practicum.dto.ViewStats;
import ru.practicum.dtos.event.*;
import ru.practicum.dtos.request.ParticipationRequestDto;
import ru.practicum.enums.State;
import ru.practicum.enums.StateAction;
import ru.practicum.enums.Status;
import ru.practicum.error.exception.*;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.practicum.model.QEvent.event;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StatClient statClient;


    @Override
    public List<EventFullDto> getAllEventsByUserId(Long userId, Integer from, Integer size) {
        log.info("Getting all events of user {}", userId);
        checkUserExists(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        Page<Event> eventPage = eventRepository.findAllByInitiatorId(userId, pageRequest);
        List<EventFullDto> events = eventPage.getContent().stream()
                .map(EventMapper::toEventFullDto)
                .toList();
        return events.isEmpty() ? Collections.emptyList() : events;
    }

    @Override
    @Transactional
    public EventFullDto addNewEvent(Long userId, NewEventDto newEventDto) {
        log.info("Adding new event by user {}", userId);
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Date of event {} is before now plus 2 hours", newEventDto.getEventDate());
            throw new ValidationException("Date of event is before now plus 2 hours");
        }

        User user = findUserById(userId);
        Category category = findCategoryById(newEventDto.getCategory());
        return EventMapper.toEventFullDto(eventRepository.save(EventMapper.mapToEvent(newEventDto, category, user)));
    }

    @Override
    public EventFullDto getEventOfUser(Long userId, Long eventId) {
        log.info("Getting event of user {}", userId);
        checkUserExists(userId);
        Event event = findEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            log.warn("User with id {} is not initiator of event with id {}", userId, eventId);
            throw new ValidationException("User is not initiator of event");
        }
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest eventUserRequest) {
        log.info("Updating event of user {}", userId);
        checkUserExists(userId);
        Event event = findEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            log.warn("User with id {} is not initiator of event with id {}", userId, eventId);
            throw new ValidationException("User is not initiator of event");
        }
        if (event.getState() == State.PUBLISHED) {
            log.error("Event with id {} is already published", eventId);
            throw new InvalidStateException("You can't edit a published event");
        }
        Optional.ofNullable(eventUserRequest.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(eventUserRequest.getDescription()).ifPresent(event::setDescription);
        if (eventUserRequest.getEventDate() != null) {
            if (eventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                log.error("Date of event {} is before now plus 2 hour", eventUserRequest.getEventDate());
                throw new ValidationException("Date of event is before now plus 2 hour");
            }
            event.setEventDate(eventUserRequest.getEventDate());
        }
        if (eventUserRequest.getLocation() != null) {
            event.setLat(eventUserRequest.getLocation().getLat());
            event.setLon(eventUserRequest.getLocation().getLon());
        }
        Optional.ofNullable(eventUserRequest.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(eventUserRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(eventUserRequest.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(eventUserRequest.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(eventUserRequest.getStateAction()).ifPresent(stateAction -> {
            if (stateAction.equals(StateAction.CANCEL_REVIEW)) {
                event.setState(State.CANCELED);
            } else if (stateAction.equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
            }
        });
        if (eventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findById(eventUserRequest.getCategory()).orElseThrow(
                    () -> new EntityNotFoundException("Category not found"));
            event.setCategory(category);
        }
        Event savedEvent = eventRepository.save(event);
        log.debug("Saved event: {}", savedEvent);
        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfUserEvent(Long userId, Long eventId) {
        log.info("Getting requests of user {} for event {}", userId, eventId);
        checkUserExists(userId);
        checkEventExists(eventId);
        return requestRepository.findByEventId(eventId)
                .stream().map(RequestMapper::toParticipationRequestDto).toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsStatusOfUserEvent(
            Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        log.info("Updating requests status of user {} for event {}", userId, eventId);
        checkUserExists(userId);
        Event event = findEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            log.warn("User with id {} is not initiator of event with id {}", userId, eventId);
            throw new SelfParticipationException("User is not initiator of event");
        }
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            log.error("Application confirmation is disabled for this event with ID:{}", eventId);
            throw new ValidationException("Application confirmation is disabled for this event with ID:" + eventId);
        }

        if (Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
            log.error("The participant limit for event with ID: {} has been reached", eventId);
            throw new ParticipantLimitReachedException("The participant limit for event with ID:"
                    + eventId + " has been reached");
        }
        List<Long> requestIds = updateRequest.getRequestIds();
        List<Request> requests = requestRepository.findAllById(requestIds);

        log.debug("Found requests: {}", requests);
        if (!requests.stream()
                .allMatch(r -> r.getEvent().getId().equals(eventId))) {
            log.error("One or more requests do not belong to event with ID:{}", eventId);
            throw new ValidationException("All applications must be related to one event");
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        switch (updateRequest.getStatus()) {
            case CONFIRMED:
                for (Request request : requests) {
                    if (request.getStatus() != Status.PENDING) {
                        log.warn("Application ID: {} is not in the pending status", request.getId());
                        throw new InvalidStateException("Only applications with a pending status can be confirmed");
                    }
                    log.debug("Current confirmed requests: {}, Participant limit: {}",
                            event.getConfirmedRequests(), event.getParticipantLimit());
                    if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                        log.warn("Cannot confirm request due to participant limit being reached.");
                        request.setStatus(Status.REJECTED);
                        rejectedRequests.add(request);
                    } else {
                        request.setStatus(Status.CONFIRMED);
                        confirmedRequests.add(request);
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    }
                }
                break;
            case REJECTED:
                for (Request request : requests) {
                    if (request.getStatus() != Status.PENDING) {
                        log.error("Application ID: {} is not in the pending status", request.getId());
                        throw new ValidationException("Only applications with a pending status can be confirmed");
                    }
                    request.setStatus(Status.REJECTED);
                    rejectedRequests.add(request);
                }
                break;
            default:
                log.error("Invalid request status");
                throw new ValidationException("Invalid request status");
        }
        requestRepository.saveAll(Stream.concat(confirmedRequests.stream(), rejectedRequests.stream()).toList());
        eventRepository.save(event);
        return new EventRequestStatusUpdateResult(
                confirmedRequests.stream()
                        .map(RequestMapper::toParticipationRequestDto)
                        .toList(),
                rejectedRequests.stream()
                        .map(RequestMapper::toParticipationRequestDto)
                        .toList());
    }

    //Поиск событий. Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия
    @Override
    public List<EventFullDto> getAdminEvents(List<Long> userIds, List<State> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                             Integer from, Integer size) {
        log.info("Getting admin events with users {}, states {}, categories {}, range start {}, range end {}",
                userIds, states, categories, rangeStart, rangeEnd);
        Page<Event> eventPage;
        Pageable pageable = createPageable(from, size, Sort.by(Sort.Direction.ASC, "createdOn"));
        BooleanBuilder queryBuilder = new BooleanBuilder();
        applyDateRangeFilter(rangeStart, rangeEnd, queryBuilder);

        if (userIds != null && !userIds.isEmpty()) {
            queryBuilder.and(event.initiator.id.in(userIds));
        }
        if (categories != null && !categories.isEmpty()) {
            queryBuilder.and(event.category.id.in(categories));
        }
        if (states != null && !states.isEmpty()) {
            queryBuilder.and(event.state.in(states));
        }

        if (queryBuilder.getValue() != null) {
            eventPage = eventRepository.findAll(queryBuilder, pageable);
        } else {
            eventPage = eventRepository.findAll(pageable);
        }
        List<Event> result = eventPage.getContent();
        setViews(result);
        return result.stream()
                .map(EventMapper::toEventFullDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEventOfAdmin(Long eventId, UpdateEventAdminRequest adminDto) {
        log.info("Updating event of Admin with id {}", eventId);
        Event event = findEventById(eventId);

        if (event.getState() != State.PENDING) {
            log.warn("Event with id {} is not state pending. Cannot perform this operation.", event.getId());
            throw new InvalidStateException("Event has already been published");
        }

        // Проверка даты события только если событие уже опубликовано
        if (adminDto.getEventDate() != null) {
            if (event.getPublishedOn() != null && adminDto.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
                log.warn("The start date of the event to be modified must be no earlier than one hour from" +
                        " the date of publication");
                throw new ValidationException("The start time of the event to be modified must be no earlier than one hour" +
                        " from the date of publication");
            }
        }

        // Обработка действия администратора
        if (adminDto.getStateAction() != null) {
            switch (adminDto.getStateAction()) {
                case PUBLISH_EVENT -> event.setState(State.PUBLISHED);
                case REJECT_EVENT -> event.setState(State.CANCELED);
                default -> throw new InvalidStateException("Invalid state action");
            }
        }

        // Установка даты публикации только при публикации события
        if (adminDto.getStateAction() == StateAction.PUBLISH_EVENT) {
            event.setPublishedOn(LocalDateTime.now());
        }

        Event savedEvent = updateEvent(event, adminDto.getCategoryId(), adminDto.getLocation(), adminDto.getAnnotation(),
                adminDto.getDescription(), adminDto.getEventDate(), adminDto.getPaid(), adminDto.getParticipantLimit(),
                adminDto.getRequestModeration(), adminDto.getTitle());

        setViews(List.of(savedEvent));
        log.info("Event {} updated from admin", savedEvent);
        return EventMapper.toEventFullDto(savedEvent);
    }

    //Получение событий с возможностью фильтрации
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getShortEventPublicByFilter(String text,
                                                           List<Long> categories,
                                                           Boolean paid,
                                                           LocalDateTime rangeStart,
                                                           LocalDateTime rangeEnd,
                                                           boolean onlyAvailable,
                                                           String sort,
                                                           int from,
                                                           int size) {
        log.info("Getting event list by filter: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, " +
                        "onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        Page<Event> events;
        Pageable pageable;
        switch (sort) {
            case "EVENT_DATE" -> pageable = createPageable(from, size, Sort.by(Sort.Direction.ASC, "eventDate"));
            case "VIEWS" -> pageable = createPageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
            case null -> pageable = createPageable(from, size, Sort.unsorted());
            default -> {
                log.warn("Unsupported sort type");
                throw new InvalidStateException("Unsupported sort type");
            }
        }
        BooleanBuilder queryBuilder = new BooleanBuilder();
        applyDateRangeFilter(rangeStart, rangeEnd, queryBuilder);
        if (text != null && !text.isBlank()) {
            queryBuilder.and(event.title.containsIgnoreCase(text).or(event.annotation.containsIgnoreCase(text)));
        }
        if (categories != null && !categories.isEmpty()) {
            queryBuilder.and(event.category.id.in(categories));
        }
        if (paid != null) {
            queryBuilder.and(event.paid.eq(paid));
        }
        if (onlyAvailable) {
            queryBuilder.and(event.confirmedRequests.gt(0)
                    .and(event.participantLimit.gt(event.confirmedRequests)));
        }
        if (queryBuilder.getValue() != null) {
            events = eventRepository.findAll(queryBuilder, pageable);
        } else {
            events = eventRepository.findAll(pageable);
        }
        List<Event> result = events.getContent();
        setViews(result);
        if (sort != null && sort.equals("VIEWS")) {
            result = result.stream()
                    .sorted(Comparator.comparing(Event::getViews))
                    .toList();
        }
        return result.stream()
                .map(EventMapper::mapToShortDto)
                .toList();
    }

    @Override
    public EventFullDto getPublicEventById(Long eventId) {
        log.info("Getting full event by id: {}", eventId);
        Event event = findEventById(eventId);
        if (event.getState() != State.PUBLISHED) {
            log.warn("Event with Id={} not published", eventId);
            throw new EntityNotFoundException("Event with Id=" + eventId + " not found");
        }
        setViews(List.of(event));
        return EventMapper.toEventFullDto(event);
    }

    private List<ViewStats> getViews(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .toList();

        List<ViewStats> viewStats = statClient.getViewStats(LocalDateTime.now().minusHours(1),
                LocalDateTime.now(), uris, true);
        log.info("Получены данные статистики: {}", viewStats);
        if (viewStats == null) {
            return Collections.emptyList();
        }
        return viewStats;
    }

    private void setViews(List<Event> events) {
        if (events.isEmpty()) {
            return;
        }
        Map<String, Long> mapUriAndHits = getViews(events).stream()
                .collect(Collectors.toMap(ViewStats::getUri, ViewStats::getHits));

        for (Event event : events) {
            event.setViews(mapUriAndHits.getOrDefault("/events/" + event.getId(), 0L));
        }
    }

    private Event updateEvent(Event event, Long categoryId, Location location, String annotation, String description,
                              LocalDateTime eventDate, Boolean paid, Integer participantLimit,
                              Boolean requestModeration, String title) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElseThrow(
                    () -> new EntityNotFoundException("Category not found"));
            event.setCategory(category);
        }
        if (location != null) {
            event.setLat(location.getLat());
            event.setLon(location.getLon());
        }

        Optional.ofNullable(annotation).ifPresent(event::setAnnotation);
        Optional.ofNullable(description).ifPresent(event::setDescription);
        Optional.ofNullable(eventDate).ifPresent(event::setEventDate);
        Optional.ofNullable(paid).ifPresent(event::setPaid);
        Optional.ofNullable(participantLimit).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(requestModeration).ifPresent(event::setRequestModeration);
        Optional.ofNullable(title).ifPresent(event::setTitle);

        return eventRepository.save(event);
    }

    private Pageable createPageable(int from, int size, Sort sort) {
        log.debug("Create Pageable with offset from {}, size {}", from, size);
        return PageRequest.of(from / size, size, sort);
    }

    private void applyDateRangeFilter(LocalDateTime rangeStart, LocalDateTime rangeEnd, BooleanBuilder queryBuilder) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                log.warn("Start time must be not after end time");
                throw new ValidationException("Start time must be not after end time");
            }
            queryBuilder.and(event.eventDate.between(rangeStart, rangeEnd));
        } else if (rangeStart == null && rangeEnd != null) {
            queryBuilder.and(event.eventDate.before(rangeEnd));
        } else if (rangeStart != null) {
            queryBuilder.and(event.eventDate.after(rangeStart));
        }
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("User with id {} not found", userId);
            throw new EntityNotFoundException("User with ID: " + userId + " not found");
        }
    }

    private void checkEventExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.warn("Event with id {} not found", eventId);
            throw new EntityNotFoundException("Event with ID: " + eventId + " not found");
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new EntityNotFoundException("Category with id={} not found"));
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Event with id={} not found"));
    }

}
