package com.tourplanner.backend.service;

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
class ImportExportServiceTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private ImportExportService importExportService;

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
        testTour.setDescription("A beautiful hike");
        testTour.setFrom("Vienna");
        testTour.setTo("Salzburg");
        testTour.setTransportType("Hiking");
        testTour.setDistance(300.0);
        testTour.setEstimatedTime(Duration.ofMinutes(180));
        testTour.setUser(testUser);

        TourLog log = new TourLog();
        log.setId(1L);
        log.setDateTime(LocalDateTime.now());
        log.setComment("Great!");
        log.setDifficulty(5);
        log.setTotalDistance(10.0);
        log.setTotalTime(Duration.ofMinutes(120));
        log.setRating(5);
        log.setTour(testTour);
        testTour.getTourLogs().add(log);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        importExportService = new ImportExportService(tourRepository, userRepository);
    }

    @Test
    void exportAllTours_Success() {
        // Arrange
        when(tourRepository.findByUserId(1L)).thenReturn(Arrays.asList(testTour));

        // Act
        String result = importExportService.exportAllTours();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Mountain Hike"));
        assertTrue(result.contains("exportVersion"));
    }

    @Test
    void exportTour_Success() {
        // Arrange
        when(tourRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTour));

        // Act
        String result = importExportService.exportTour(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Mountain Hike"));
    }

    @Test
    void importTours_Success() {
        // Arrange
        String jsonData = "{\"exportVersion\":\"1.0\",\"exportDate\":\"2024-01-01T00:00:00\",\"tours\":[{\"name\":\"New Tour\",\"description\":\"Desc\",\"from\":\"A\",\"to\":\"B\",\"transportType\":\"Car\",\"distance\":100.0,\"estimatedTimeMinutes\":60,\"logs\":[]}]}";
        when(tourRepository.save(any(Tour.class))).thenAnswer(invocation -> {
            Tour tour = invocation.getArgument(0);
            tour.setId(2L);
            return tour;
        });

        // Act
        int result = importExportService.importTours(jsonData);

        // Assert
        assertEquals(1, result);
        verify(tourRepository).save(any(Tour.class));
    }

    @Test
    void importTours_EmptyData_ReturnsZero() {
        // Arrange
        String jsonData = "{\"exportVersion\":\"1.0\",\"tours\":[]}";

        // Act
        int result = importExportService.importTours(jsonData);

        // Assert
        assertEquals(0, result);
    }
}
