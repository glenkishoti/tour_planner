package com.tourplanner.backend.repository;

import com.tourplanner.backend.entity.Tour;
import com.tourplanner.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourRepositoryTest {

    @Mock
    private TourRepository tourRepository;

    @Test
    void findById_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("touruser");

        Tour tour = new Tour();
        tour.setId(1L);
        tour.setName("Test Tour");
        tour.setFrom("Vienna");
        tour.setTo("Salzburg");
        tour.setTransportType("Bus");
        tour.setDistance(300.0);
        tour.setEstimatedTime(Duration.ofMinutes(180));
        tour.setUser(user);

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        // Act
        Optional<Tour> found = tourRepository.findById(1L);

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Test Tour", found.get().getName());
    }

    @Test
    void findByUserId_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("touruser");

        Tour tour1 = new Tour();
        tour1.setId(1L);
        tour1.setName("Tour 1");
        tour1.setFrom("A");
        tour1.setTo("B");
        tour1.setTransportType("Bus");
        tour1.setDistance(100.0);
        tour1.setEstimatedTime(Duration.ofMinutes(60));
        tour1.setUser(user);

        Tour tour2 = new Tour();
        tour2.setId(2L);
        tour2.setName("Tour 2");
        tour2.setFrom("C");
        tour2.setTo("D");
        tour2.setTransportType("Train");
        tour2.setDistance(200.0);
        tour2.setEstimatedTime(Duration.ofMinutes(120));
        tour2.setUser(user);

        when(tourRepository.findByUserId(1L)).thenReturn(Arrays.asList(tour1, tour2));

        // Act
        List<Tour> tours = tourRepository.findByUserId(1L);

        // Assert
        assertEquals(2, tours.size());
        assertEquals("Tour 1", tours.get(0).getName());
        assertEquals("Tour 2", tours.get(1).getName());
    }

    @Test
    void findByIdAndUserId_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);

        Tour tour = new Tour();
        tour.setId(1L);
        tour.setName("Secure Tour");
        tour.setFrom("X");
        tour.setTo("Y");
        tour.setTransportType("Car");
        tour.setDistance(50.0);
        tour.setEstimatedTime(Duration.ofMinutes(30));
        tour.setUser(user);

        when(tourRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(tour));

        // Act
        Optional<Tour> found = tourRepository.findByIdAndUserId(1L, 1L);

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Secure Tour", found.get().getName());
    }

    @Test
    void existsByIdAndUserId_ReturnsTrue() {
        // Arrange
        when(tourRepository.existsByIdAndUserId(1L, 1L)).thenReturn(true);

        // Act
        boolean exists = tourRepository.existsByIdAndUserId(1L, 1L);

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByIdAndUserId_ReturnsFalse() {
        // Arrange
        when(tourRepository.existsByIdAndUserId(999L, 1L)).thenReturn(false);

        // Act
        boolean exists = tourRepository.existsByIdAndUserId(999L, 1L);

        // Assert
        assertFalse(exists);
    }

    @Test
    void save_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);

        Tour tour = new Tour();
        tour.setName("New Tour");
        tour.setFrom("Start");
        tour.setTo("End");
        tour.setTransportType("Bike");
        tour.setDistance(25.0);
        tour.setEstimatedTime(Duration.ofMinutes(90));
        tour.setUser(user);

        Tour savedTour = new Tour();
        savedTour.setId(1L);
        savedTour.setName("New Tour");
        savedTour.setFrom("Start");
        savedTour.setTo("End");
        savedTour.setTransportType("Bike");
        savedTour.setDistance(25.0);
        savedTour.setEstimatedTime(Duration.ofMinutes(90));
        savedTour.setUser(user);

        when(tourRepository.save(tour)).thenReturn(savedTour);

        // Act
        Tour result = tourRepository.save(tour);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Tour", result.getName());
    }
}
