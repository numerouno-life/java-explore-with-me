package event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.MainService;
import ru.practicum.dtos.event.EventRequestStatusUpdateRequest;
import ru.practicum.enums.State;
import ru.practicum.enums.Status;
import ru.practicum.enums.UpdateStatus;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MainService.class)
@AutoConfigureMockMvc
@Transactional
class EventServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    void shouldReturn409WhenParticipantLimitReached() throws Exception {
        // Arrange: Создаем пользователя, событие и запросы
        User user = userRepository.save(new User(1L, "user@example.com", "John Doe"));

        Event event = Event.builder()
                .annotation("Test annotation")
                .category(null)
                .confirmedRequests(1)
                .createdOn(LocalDateTime.now())
                .description("Test description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .initiator(user)
                .lat(55.7522f)
                .lon(37.6156f)
                .paid(false)
                .participantLimit(1)
                .publishedOn(null)
                .requestModeration(true)
                .state(State.PENDING)
                .title("Test Event")
                .build();
        event = eventRepository.save(event);

        Request request1 = Request.builder()
                .event(event)
                .requester(user)
                .status(Status.CONFIRMED)
                .build();
        requestRepository.save(request1);

        Long requestId = request1.getId();

        EventRequestStatusUpdateRequest updateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(requestId))
                .status(UpdateStatus.CONFIRMED)
                .build();

        // Act & Assert: Проверяем, что возвращается статус 409
        mockMvc.perform(patch("/users/{userId}/events/{eventId}/requests", user.getId(), event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict()) // Ожидаем статус 409
                .andExpect(jsonPath("$.message").value("The participant limit for event with ID:1 has been reached"))
                .andExpect(jsonPath("$.reason").value("ParticipantLimitReachedException"))
                .andExpect(jsonPath("$.status").value("CONFLICT"));
    }

    @Test
    void shouldUpdateRequestStatusToConfirmed() throws Exception {
        // Arrange: Создаем пользователя, событие и запросы
        User user = userRepository.save(new User(1L, "user@example.com", "John Doe"));

        Event event = Event.builder()
                .annotation("Test annotation")
                .category(null)
                .confirmedRequests(0)
                .createdOn(LocalDateTime.now())
                .description("Test description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .initiator(user)
                .lat(55.7522f)
                .lon(37.6156f)
                .paid(false)
                .participantLimit(2)
                .publishedOn(null)
                .requestModeration(true)
                .state(State.PENDING)
                .title("Test Event")
                .build();
        event = eventRepository.save(event);

        Request request1 = Request.builder()
                .event(event)
                .requester(user)
                .status(Status.PENDING)
                .build();
        requestRepository.save(request1);

        Long requestId = request1.getId();

        EventRequestStatusUpdateRequest updateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(requestId))
                .status(UpdateStatus.CONFIRMED)
                .build();

        // Act & Assert: Проверяем, что статус запроса обновлен до CONFIRMED
        mockMvc.perform(patch("/users/{userId}/events/{eventId}/requests", user.getId(), event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmedRequests[0].id").value(requestId))
                .andExpect(jsonPath("$.confirmedRequests[0].status").value("CONFIRMED"));
    }

    @Test
    void shouldReturn400IfRequestIsInvalid() throws Exception {
        EventRequestStatusUpdateRequest invalidRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of())
                .status(null)
                .build();

        mockMvc.perform(patch("/users/{userId}/events/{eventId}/requests", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

}