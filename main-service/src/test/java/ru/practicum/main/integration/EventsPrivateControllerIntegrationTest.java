package ru.practicum.main.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.MainService;
import ru.practicum.RatingClient;
import ru.practicum.RatingResponseDto;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = MainService.class)
@AutoConfigureMockMvc
public class EventsPrivateControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private RatingClient ratingClient;

    private static final Long USER_ID = 1L;
    private static final Long EVENT_ID = 1L;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setId(USER_ID);
        user.setName("Test User");
        user.setEmail("test@example.com");
        userRepository.save(user);

        Event event = new Event();
        event.setId(EVENT_ID);
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        eventRepository.save(event);

        doNothing().when(ratingClient).likeEvent(EVENT_ID, USER_ID);

        RatingResponseDto mockResponse = new RatingResponseDto();
        mockResponse.setLikes(5L);
        mockResponse.setDislikes(3L);
        mockResponse.setTotalRating(2L); // 5 - 3 = 2

        when(ratingClient.totalRating(EVENT_ID)).thenReturn(mockResponse);
    }

    @Test
    public void testLikeEvent() {
        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/users/{userId}/events/{eventId}/like",
                null,
                Void.class,
                USER_ID,
                EVENT_ID
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testDislikeEvent() {
        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/users/{userId}/events/{eventId}/dislike",
                null,
                Void.class,
                USER_ID,
                EVENT_ID
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testTotalRating() {
        ResponseEntity<RatingResponseDto> response = restTemplate.getForEntity(
                "/users/{userId}/events/{eventId}/rating",
                RatingResponseDto.class,
                USER_ID,
                EVENT_ID
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        RatingResponseDto body = response.getBody();
        assertNotNull(body);
        assertEquals(5, body.getLikes());
        assertEquals(3, body.getDislikes());
        assertEquals(2, body.getTotalRating());
    }
}