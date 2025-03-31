package ru.practicum.service.UserRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dtos.request.ParticipationRequestDto;
import ru.practicum.enums.State;
import ru.practicum.enums.Status;
import ru.practicum.error.exception.*;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserRequestServiceImpl implements UserRequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getAllUserRequests(Long userId) {
        log.info("Get all user requests with userId={}", userId);
        checkUserExists(userId);
        List<ParticipationRequestDto> result = requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
        log.info("Found {} requests for user with id={}", result.size(), userId);
        return result;
    }

    @Transactional
    @Override
    public ParticipationRequestDto createUserRequest(Long userId, Long eventId) {
        log.info("Create user request with userId={} and eventId={}", userId, eventId);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id=%d not found", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event with id=%d not found", eventId)));

        if (!event.getState().equals(State.PUBLISHED)) {
            log.info("Event with id={} not published", eventId);
            throw new InvalidStateException(String.format("Event with id=%d not published", eventId));
        }

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            log.info("Request already created with userId={} and eventId={}", userId, eventId);
            throw new InvalidStateException(String.format(
                    "Request already created with userId=%d and eventId=%d", userId, eventId));
        }

        if (event.getInitiator().getId().equals(userId)) {
            log.info("User with id={} is initiator of event with id={}", userId, eventId);
            throw new SelfParticipationException(String.format("The event initiator with id=%d " +
                    "cannot add a request to participate in his event with id=%d", userId, eventId));
        }

        Integer requestCount = requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED);
        log.info("Total participation requests for event {}: {}", eventId, requestCount);

        if (event.getParticipantLimit() > 0 && requestCount >= event.getParticipantLimit()) {
            log.info("The event with id={} has already reached the participant limit", eventId);
            throw new ParticipantLimitReachedException(String.format(
                    "The event with id=%d has already reached the participant limit", eventId));
        }

        Request newRequest = new Request();
        newRequest.setEvent(event);
        newRequest.setRequester(user);
        newRequest.setCreated(LocalDateTime.now());

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            log.info("The event with id={} does not require moderation", eventId);
            newRequest.setStatus(Status.CONFIRMED);

            event.setConfirmedRequests(Optional.ofNullable(event.getConfirmedRequests()).orElse(0) + 1);

            eventRepository.save(event);
        } else {
            log.info("The event with id={} requires moderation", eventId);
            newRequest.setStatus(Status.PENDING);
        }
        Request result = requestRepository.save(newRequest);
        log.info("Saved new request with id={}", newRequest.getId());
        return RequestMapper.toParticipationRequestDto(result);
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        log.info("Cancel user request with userId={} and requestId={}", userId, requestId);
        checkUserExists(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Request with id=%d not found", requestId)));
        if (!request.getRequester().getId().equals(userId)) {
            log.info("Request with id={} not created by user with id={}", requestId, userId);
            throw new ValidationException(String.format(
                    "Request with id=%d not created by user with id=%d", requestId, userId));
        }
        request.setStatus(Status.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.info("User with id={} not found", userId);
            throw new EntityNotFoundException(String.format("User with id=%d not found", userId));
        }
    }
}
