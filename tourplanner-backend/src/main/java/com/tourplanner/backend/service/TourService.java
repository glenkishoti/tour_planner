package com.tourplanner.backend.service;

import com.tourplanner.backend.dto.TourRequest;
import com.tourplanner.backend.dto.TourResponse;
import com.tourplanner.backend.entity.Tour;
import com.tourplanner.backend.entity.User;
import com.tourplanner.backend.repository.TourRepository;
import com.tourplanner.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TourService {

    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    public TourResponse createTour(TourRequest request) {
        log.info("Creating new tour: {}", request.getName());

        User user = getCurrentUser();

        Tour tour = new Tour();
        mapRequestToEntity(request, tour);
        tour.setUser(user);

        Tour savedTour = tourRepository.save(tour);
        log.info("Tour created successfully with id: {}", savedTour.getId());

        return mapToResponse(savedTour);
    }

    @Transactional(readOnly = true)
    public List<TourResponse> getAllToursForCurrentUser() {
        User user = getCurrentUser();
        log.debug("Fetching all tours for user: {}", user.getUsername());

        return tourRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TourResponse getTourById(Long id) {
        User user = getCurrentUser();
        log.debug("Fetching tour with id: {} for user: {}", id, user.getUsername());

        Tour tour = tourRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tour not found with id: " + id));

        return mapToResponse(tour);
    }

    public TourResponse updateTour(Long id, TourRequest request) {
        User user = getCurrentUser();
        log.info("Updating tour with id: {} for user: {}", id, user.getUsername());

        Tour tour = tourRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tour not found with id: " + id));

        mapRequestToEntity(request, tour);

        Tour updatedTour = tourRepository.save(tour);
        log.info("Tour updated successfully with id: {}", updatedTour.getId());

        return mapToResponse(updatedTour);
    }

    public void deleteTour(Long id) {
        User user = getCurrentUser();
        log.info("Deleting tour with id: {} for user: {}", id, user.getUsername());

        Tour tour = tourRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tour not found with id: " + id));

        tourRepository.delete(tour);
        log.info("Tour deleted successfully with id: {}", id);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private void mapRequestToEntity(TourRequest request, Tour tour) {
        tour.setName(request.getName());
        tour.setDescription(request.getDescription());
        tour.setFrom(request.getFrom());
        tour.setTo(request.getTo());
        tour.setTransportType(request.getTransportType());
        tour.setDistance(request.getDistance());
        tour.setEstimatedTime(Duration.ofMinutes(request.getEstimatedTimeMinutes()));
    }

    private TourResponse mapToResponse(Tour tour) {
        return TourResponse.builder()
                .id(tour.getId())
                .name(tour.getName())
                .description(tour.getDescription())
                .from(tour.getFrom())
                .to(tour.getTo())
                .transportType(tour.getTransportType())
                .distance(tour.getDistance())
                .estimatedTimeMinutes(tour.getEstimatedTime().toMinutes())
                .imagePath(tour.getImagePath())
                .build();
    }
}
