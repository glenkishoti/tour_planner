package com.tourplanner.backend.service;

import com.tourplanner.backend.dto.TourRequest;
import com.tourplanner.backend.dto.TourResponse;
import com.tourplanner.backend.entity.Tour;
import com.tourplanner.backend.entity.User;
import com.tourplanner.backend.repository.TourRepository;
import com.tourplanner.backend.repository.UserRepository;
import com.tourplanner.backend.service.client.OpenRouteServiceClient;
import com.tourplanner.backend.service.client.RouteInfo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OpenRouteServiceClient openRouteServiceClient;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TourService tourService;

    private User testUser;
    private Tour testTour;
    private TourRequest tourRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");

        testTour = new Tour();
        testTour.setId(1L);
        testTour.setName("Test Tour");
        testTour.setDescription("Test Description");
        testTour.setFrom("Vienna");
        testTour.setTo("Salzburg");
        testTour.setTransportType("Bus");
        testTour.setDistance(300.0);
        testTour.setEstimatedTime(Duration.ofMinutes(180));
        testTour.setUser(testUser);

        tourRequest = TourRequest.builder()
                .name("Test Tour")
                .description("Test Description")
                .from("Vienna")
                .to("Salzburg")
                .transportType("Bus")
                .distance(300.0)
                .estimatedTimeMinutes(180L)
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    }

    @Test
    void createTour_Success() {
        // Arrange
        RouteInfo routeInfo = new RouteInfo();
        routeInfo.setDistance(300.0);
        routeInfo.setDurationInSeconds(10800L);
        
        when(openRouteServiceClient.getRouteInfo(any(), any(), any())).thenReturn(routeInfo);
        when(tourRepository.save(any(Tour.class))).thenReturn(testTour);

        // Act
        TourResponse result = tourService.createTour(tourRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Test Tour", result.getName());
        verify(tourRepository).save(any(Tour.class));
    }

    @Test
    void getAllToursForCurrentUser_Success() {
        // Arrange
        when(tourRepository.findByUserId(1L)).thenReturn(Arrays.asList(testTour));

        // Act
        List<TourResponse> result = tourService.getAllToursForCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Tour", result.get(0).getName());
    }

    @Test
    void getTourById_Success() {
        // Arrange
        when(tourRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTour));

        // Act
        TourResponse result = tourService.getTourById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Tour", result.getName());
    }

    @Test
    void getTourById_NotFound_ThrowsException() {
        // Arrange
        when(tourRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> tourService.getTourById(1L));
    }

    @Test
    void updateTour_Success() {
        // Arrange
        RouteInfo routeInfo = new RouteInfo();
        routeInfo.setDistance(300.0);
        routeInfo.setDurationInSeconds(10800L);
        
        when(tourRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTour));
        when(openRouteServiceClient.getRouteInfo(any(), any(), any())).thenReturn(routeInfo);
        when(tourRepository.save(any(Tour.class))).thenReturn(testTour);

        // Act
        TourResponse result = tourService.updateTour(1L, tourRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Test Tour", result.getName());
    }

    @Test
    void deleteTour_Success() {
        // Arrange
        when(tourRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTour));

        // Act
        tourService.deleteTour(1L);

        // Assert
        verify(tourRepository).delete(testTour);
    }
}
