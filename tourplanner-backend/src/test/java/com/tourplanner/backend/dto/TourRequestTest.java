package com.tourplanner.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TourRequestTest {

    @Test
    void builderCreatesTourRequest() {
        TourRequest request = TourRequest.builder()
                .name("Test Tour")
                .description("A test tour")
                .from("Vienna")
                .to("Salzburg")
                .transportType("Bus")
                .distance(300.0)
                .estimatedTimeMinutes(180L)
                .build();

        assertNotNull(request);
        assertEquals("Test Tour", request.getName());
        assertEquals("Vienna", request.getFrom());
        assertEquals(300.0, request.getDistance());
    }

    @Test
    void gettersAndSettersWork() {
        TourRequest request = new TourRequest();
        request.setName("New Tour");
        request.setDescription("Description");
        request.setFrom("Start");
        request.setTo("End");
        request.setTransportType("Train");
        request.setDistance(100.0);
        request.setEstimatedTimeMinutes(60L);

        assertEquals("New Tour", request.getName());
        assertEquals("Description", request.getDescription());
        assertEquals("Start", request.getFrom());
        assertEquals("End", request.getTo());
        assertEquals("Train", request.getTransportType());
        assertEquals(100.0, request.getDistance());
        assertEquals(60L, request.getEstimatedTimeMinutes());
    }

    @Test
    void equalsAndHashCode() {
        TourRequest request1 = TourRequest.builder()
                .name("Test")
                .description("Desc")
                .from("A")
                .to("B")
                .transportType("Bus")
                .distance(100.0)
                .estimatedTimeMinutes(60L)
                .build();

        TourRequest request2 = TourRequest.builder()
                .name("Test")
                .description("Desc")
                .from("A")
                .to("B")
                .transportType("Bus")
                .distance(100.0)
                .estimatedTimeMinutes(60L)
                .build();

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }
}
