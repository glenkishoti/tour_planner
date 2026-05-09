package com.tourplanner.backend.service;

import com.tourplanner.backend.dto.TourResponse;
import com.tourplanner.backend.dto.TourLogResponse;
import com.tourplanner.backend.entity.Tour;
import com.tourplanner.backend.entity.TourLog;
import com.tourplanner.backend.entity.User;
import com.tourplanner.backend.repository.TourRepository;
import com.tourplanner.backend.repository.TourLogRepository;
import com.tourplanner.backend.repository.UserRepository;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private TourLogRepository tourLogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SearchService searchService;

    private User testUser;
    private Tour testTour;
    private TourLog testTourLog;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testTour = new Tour();
        testTour.setId(1L);
        testTour.setName("Mountain Hike");
        testTour.setDescription("A beautiful mountain hike");
        testTour.setFrom("Vienna");
        testTour.setTo("Salzburg");
        testTour.setTransportType("Hiking");
        testTour.setDistance(300.0);
        testTour.setEstimatedTime(Duration.ofMinutes(180));

        testTourLog = new TourLog();
        testTourLog.setId(1L);
        testTourLog.setDateTime(LocalDateTime.now());
        testTourLog.setComment("Amazing experience!");
        testTourLog.setDifficulty(5);
        testTourLog.setTotalDistance(10.0);
        testTourLog.setTotalTime(Duration.ofMinutes(120));
        testTourLog.setRating(5);
        testTourLog.setTour(testTour);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    }

    @Test
    void searchTours_WithQuery_ReturnsMatchingTours() {
        // Arrange
        when(tourRepository.searchTours(1L, "%mountain%")).thenReturn(Arrays.asList(testTour));

        // Act
        List<TourResponse> result = searchService.searchTours("mountain");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mountain Hike", result.get(0).getName());
    }

    @Test
    void searchTours_EmptyQuery_ReturnsAllTours() {
        // Arrange
        when(tourRepository.findByUserId(1L)).thenReturn(Arrays.asList(testTour));

        // Act
        List<TourResponse> result = searchService.searchTours("");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void searchTourLogs_WithQuery_ReturnsMatchingLogs() {
        // Arrange
        when(tourLogRepository.searchTourLogs(1L, "%amazing%")).thenReturn(Arrays.asList(testTourLog));

        // Act
        List<TourLogResponse> result = searchService.searchTourLogs("amazing");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Amazing experience!", result.get(0).getComment());
    }

    @Test
    void searchToursAdvanced_WithFilters_ReturnsFilteredTours() {
        // Arrange
        when(tourRepository.findByUserId(1L)).thenReturn(Arrays.asList(testTour));

        // Act
        List<TourResponse> result = searchService.searchToursAdvanced(
                "Mountain", null, null, null, 200.0, 400.0, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mountain Hike", result.get(0).getName());
    }
}
