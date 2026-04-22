package com.tourplanner.backend.service;

import com.tourplanner.backend.dto.StatisticsDTO;
import com.tourplanner.backend.entity.Tour;
import com.tourplanner.backend.entity.TourLog;
import com.tourplanner.backend.entity.User;
import com.tourplanner.backend.repository.TourRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private StatisticsService statisticsService;

    private User testUser;
    private Tour testTour;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testTour = new Tour();
        testTour.setId(1L);
        testTour.setName("Mountain Hike");
        testTour.setFrom("Vienna");
        testTour.setTo("Salzburg");
        testTour.setTransportType("Hiking");
        testTour.setDistance(300.0);
        testTour.setEstimatedTime(Duration.ofMinutes(180));
        testTour.setUser(testUser);

        TourLog log1 = new TourLog();
        log1.setId(1L);
        log1.setDateTime(LocalDateTime.now());
        log1.setComment("Great!");
        log1.setDifficulty(5);
        log1.setTotalDistance(10.0);
        log1.setTotalTime(Duration.ofMinutes(120));
        log1.setRating(5);
        log1.setTour(testTour);
        testTour.getTourLogs().add(log1);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    }

    @Test
    void getUserStatistics_Success() {
        // Arrange
        when(tourRepository.findByUserId(1L)).thenReturn(Arrays.asList(testTour));

        // Act
        StatisticsDTO result = statisticsService.getUserStatistics();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getOverallStats());
        assertEquals(1, result.getOverallStats().getTotalTours());
        assertEquals(1, result.getOverallStats().getTotalLogs());
        assertNotNull(result.getTourStats());
        assertEquals(1, result.getTourStats().size());
        assertNotNull(result.getTransportTypeStats());
    }

    @Test
    void getTourStatistics_Success() {
        // Arrange
        when(tourRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTour));

        // Act
        StatisticsDTO.TourStats result = statisticsService.getTourStatistics(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Mountain Hike", result.getTourName());
        assertEquals(1, result.getLogCount());
    }

    @Test
    void getUserStatistics_NoLogs_ReturnsZeroStats() {
        // Arrange
        Tour emptyTour = new Tour();
        emptyTour.setId(2L);
        emptyTour.setName("Empty Tour");
        emptyTour.setDistance(100.0);
        emptyTour.setTransportType("Car");
        emptyTour.setUser(testUser);

        when(tourRepository.findByUserId(1L)).thenReturn(Arrays.asList(emptyTour));

        // Act
        StatisticsDTO result = statisticsService.getUserStatistics();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getOverallStats().getTotalLogs());
    }
}
