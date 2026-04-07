package com.tourplanner.backend.service;

import com.tourplanner.backend.dto.TourLogRequest;
import com.tourplanner.backend.dto.TourLogResponse;
import com.tourplanner.backend.entity.Tour;
import com.tourplanner.backend.entity.TourLog;
import com.tourplanner.backend.entity.User;
import com.tourplanner.backend.repository.TourLogRepository;
import com.tourplanner.backend.repository.TourRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourLogServiceTest {

    @Mock
    private TourLogRepository tourLogRepository;

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private TourLogService tourLogService;

    private User testUser;
    private Tour testTour;
    private TourLog testTourLog;
    private TourLogRequest tourLogRequest;

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
        testTour.setUser(testUser);

        testTourLog = new TourLog();
        testTourLog.setId(1L);
        testTourLog.setDateTime(LocalDateTime.now());
        testTourLog.setComment("Great tour!");
        testTourLog.setDifficulty(5);
        testTourLog.setTotalDistance(10.5);
        testTourLog.setTotalTime(Duration.ofMinutes(120));
        testTourLog.setRating(4);
        testTourLog.setTour(testTour);

        tourLogRequest = TourLogRequest.builder()
                .dateTime(LocalDateTime.now())
                .comment("Great tour!")
                .difficulty(5)
                .totalDistance(10.5)
                .totalTimeMinutes(120L)
                .rating(4)
                .build();
    }

    @Test
    void getAllTourLogsForTour_Success() {
        // Arrange
        when(tourRepository.existsByIdAndUserId(1L, 1L)).thenReturn(true);
        when(tourLogRepository.findByTourIdAndTourUserId(1L, 1L)).thenReturn(Arrays.asList(testTourLog));

        // Act
        List<TourLogResponse> result = tourLogService.getAllTourLogsForTour(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Great tour!", result.get(0).getComment());
    }

    @Test
    void getTourLogById_Success() {
        // Arrange
        when(tourLogRepository.findByIdAndTourUserId(1L, 1L)).thenReturn(Optional.of(testTourLog));

        // Act
        TourLogResponse result = tourLogService.getTourLogById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Great tour!", result.getComment());
    }

    @Test
    void getTourLogById_NotFound_ThrowsException() {
        // Arrange
        when(tourLogRepository.findByIdAndTourUserId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> tourLogService.getTourLogById(1L));
    }

    @Test
    void deleteTourLog_Success() {
        // Arrange
        when(tourLogRepository.findByIdAndTourUserId(1L, 1L)).thenReturn(Optional.of(testTourLog));

        // Act
        tourLogService.deleteTourLog(1L);

        // Assert
        verify(tourLogRepository).delete(testTourLog);
    }

    @Test
    void getAllTourLogsForTour_TourNotFound_ThrowsException() {
        // Arrange
        when(tourRepository.existsByIdAndUserId(1L, 1L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> tourLogService.getAllTourLogsForTour(1L));
    }
}
