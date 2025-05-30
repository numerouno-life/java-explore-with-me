package ru.practicum.ratingserver.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.RatingResponseDto;
import ru.practicum.service.RatingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RatingControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private static final Long EVENT_ID = 1L;
    private static final Long USER_ID = 1L;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RatingService ratingService;

    @BeforeEach
    public void clearDatabase() {
        jdbcTemplate.execute("DELETE FROM event_ratings");
    }

    @Test
    public void testLikeEvent() {
        ResponseEntity<Void> response = likeEvent();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testDislikeEvent() {
        ResponseEntity<Void> response = dislikeEvent();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testTotalRatingAfterLike() {
        likeEvent();
        ResponseEntity<RatingResponseDto> response = getRating();
        assertEquals(1, Objects.requireNonNull(response.getBody()).getTotalRating());
    }

    @Test
    public void testTotalRatingAfterDislike() {
        dislikeEvent();
        ResponseEntity<RatingResponseDto> response = getRating();
        assertEquals(-1, Objects.requireNonNull(response.getBody()).getTotalRating());
    }

    @Test
    public void testSwitchFromLikeToDislike() {
        // Put a like
        likeEvent();

        // Checking that the rating is 1
        ResponseEntity<RatingResponseDto> afterLike = getRating();
        assertEquals(1, Objects.requireNonNull(afterLike.getBody()).getTotalRating());

        // switch to dislike
        dislikeEvent();

        // Checking that the rating is -1
        ResponseEntity<RatingResponseDto> afterDislike = getRating();
        assertEquals(-1, Objects.requireNonNull(afterDislike.getBody()).getTotalRating());

        // Checking total likes and dislikes
        assertEquals(0, afterDislike.getBody().getLikes());
        assertEquals(1, afterDislike.getBody().getDislikes());
    }

    @Test
    public void testLargeNumberOfLikes() {
        jdbcTemplate.execute("DELETE FROM event_ratings");
        System.out.println("Table 'event_ratings' cleared.");

        for (int i = 0; i < 1000; i++) {
            restTemplate.postForEntity(
                    "/events/{eventId}/like?userId={userId}",
                    null,
                    Void.class,
                    EVENT_ID,
                    USER_ID + i
            );
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM event_ratings");
            System.out.println("Data in event_ratings: " + rows);
        }

        ResponseEntity<RatingResponseDto> response = getRating();
        RatingResponseDto body = response.getBody();

        assertNotNull(body);
        assertEquals(1000, body.getLikes());
        assertEquals(0, body.getDislikes());
        assertEquals(1000, body.getTotalRating());
    }

    @Test
    public void testLargeNumberOfDislikes() {
        jdbcTemplate.execute("DELETE FROM event_ratings");
        System.out.println("Table 'event_ratings' cleared.");

        for (int i = 0; i < 1000; i++) {
            restTemplate.postForEntity(
                    "/events/{eventId}/dislike?userId={userId}",
                    null,
                    Void.class,
                    EVENT_ID,
                    USER_ID + i
            );
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM event_ratings");
            System.out.println("Data in event_ratings: " + rows);
        }

        ResponseEntity<RatingResponseDto> response = getRating();
        RatingResponseDto body = response.getBody();

        assertNotNull(body);
        assertEquals(0, body.getLikes());
        assertEquals(1000, body.getDislikes());
        assertEquals(-1000, body.getTotalRating());
    }

    @Transactional
    @Test
    public void testConcurrentLikes() throws InterruptedException {
        final Long testEventId = getUniqueEventId();
        final int threadCount = 2;
        final int likesPerThread = 10;
        final CountDownLatch latch = new CountDownLatch(threadCount);

        Runnable task = () -> {
            try {
                latch.await(); // wait for all threads to start
                for (int i = 0; i < likesPerThread; i++) {
                    Long userId = getUniqueUserId(); // Unique user ID for each thread
                    restTemplate.postForEntity(
                            "/events/{eventId}/like?userId={userId}",
                            null,
                            Void.class,
                            testEventId,
                            userId
                    );
                }
            } catch (Exception e) {
                fail("Exception in thread: " + e.getMessage());
            }
        };

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(task);
            threads.add(thread);
            thread.start();
            latch.countDown();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        ResponseEntity<RatingResponseDto> response = restTemplate.getForEntity(
                "/events/{eventId}/rating",
                RatingResponseDto.class,
                testEventId
        );

        RatingResponseDto body = response.getBody();
        assertNotNull(body);
        assertEquals(threadCount * likesPerThread, body.getLikes());
        assertEquals(0, body.getDislikes());
        assertEquals(threadCount * likesPerThread, body.getTotalRating());
    }

    //TODO Закомментирован поскольку тест занимает слишком много времени
    /*@Test
    public void testPerformance() {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 30_000; i++) {
            ratingService.likeEvent(EVENT_ID, USER_ID + i);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }*/

    private ResponseEntity<Void> likeEvent() {
        return restTemplate.postForEntity(
                "/events/{eventId}/like?userId={userId}",
                null,
                Void.class,
                EVENT_ID,
                USER_ID
        );
    }

    private ResponseEntity<Void> dislikeEvent() {
        return restTemplate.postForEntity(
                "/events/{eventId}/dislike?userId={userId}",
                null,
                Void.class,
                EVENT_ID,
                USER_ID
        );
    }

    private ResponseEntity<RatingResponseDto> getRating() {
        return restTemplate.getForEntity(
                "/events/{eventId}/rating",
                RatingResponseDto.class,
                EVENT_ID
        );
    }

    private Long getUniqueEventId() {
        return System.currentTimeMillis();
    }

    private Long getUniqueUserId() {
        return ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
    }
}
