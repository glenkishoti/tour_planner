package com.tourplanner.backend.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TourLogRequestTest {

    @Test
    void builderCreatesTourLogRequest() {
        LocalDateTime now = LocalDateTime.now();
        TourLogRequest request = TourLogRequest.builder()
                .dateTime(now)
                .comment("Great tour!")
                .difficulty(5)
                .totalDistance(10.5)
                .totalTimeMinutes(120L)
                .rating(4)
                .build();

        assertNotNull(request);
        assertEquals(now, request.getDateTime());
        assertEquals("Great tour!", request.getComment());
        assertEquals(5, request.getDifficulty());
        assertEquals(4, request.getRating());
    }

    @Test
    void gettersAndSettersWork() {
        LocalDateTime now = LocalDateTime.now();
        TourLogRequest request = new TourLogRequest();
        request.setDateTime(now);
        request.setComment("Test comment");
        request.setDifficulty(7);
        request.setTotalDistance(15.0);
        request.setTotalTimeMinutes(90L);
        request.setRating(5);

        assertEquals(now, request.getDateTime());
        assertEquals("Test comment", request.getComment());
        assertEquals(7, request.getDifficulty());
        assertEquals(15.0, request.getTotalDistance());
        assertEquals(90L, request.getTotalTimeMinutes());
        assertEquals(5, request.getRating());
    }

    @Test
    void equalsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        TourLogRequest request1 = TourLogRequest.builder()
                .dateTime(now)
                .comment("Comment")
                .difficulty(5)
                .totalDistance(10.0)
                .totalTimeMinutes(60L)
                .rating(3)
                .build();

        TourLogRequest request2 = TourLogRequest.builder()
                .dateTime(now)
                .comment("Comment")
                .difficulty(5)
                .totalDistance(10.0)
                .totalTimeMinutes(60L)
                .rating(3)
                .build();

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }
}
