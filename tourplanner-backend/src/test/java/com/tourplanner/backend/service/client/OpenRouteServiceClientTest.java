package com.tourplanner.backend.service.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OpenRouteServiceClientTest {

    @InjectMocks
    private OpenRouteServiceClient openRouteServiceClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(openRouteServiceClient, "apiKey", "");
    }

    @Test
    void getRouteInfo_NoApiKey_ReturnsFallback() {
        // Act
        RouteInfo result = openRouteServiceClient.getRouteInfo("Vienna", "Salzburg", "car");

        // Assert
        assertNotNull(result);
        assertEquals(10.0, result.getDistance());
        assertEquals(7200L, result.getDurationInSeconds());
    }

    @Test
    void getRouteInfo_DifferentTransportTypes_ReturnsFallback() {
        // Test bike
        RouteInfo bikeResult = openRouteServiceClient.getRouteInfo("A", "B", "bike");
        assertNotNull(bikeResult);
        assertEquals(10.0, bikeResult.getDistance());

        // Test hiking
        RouteInfo hikingResult = openRouteServiceClient.getRouteInfo("A", "B", "hiking");
        assertNotNull(hikingResult);

        // Test null transport type
        RouteInfo nullResult = openRouteServiceClient.getRouteInfo("A", "B", null);
        assertNotNull(nullResult);
    }
}
