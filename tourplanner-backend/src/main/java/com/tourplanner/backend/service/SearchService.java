package com.tourplanner.backend.service;

import com.tourplanner.backend.dto.TourResponse;
import com.tourplanner.backend.dto.TourLogResponse;
import com.tourplanner.backend.entity.Tour;
import com.tourplanner.backend.entity.TourLog;
import com.tourplanner.backend.entity.User;
import com.tourplanner.backend.repository.TourRepository;
import com.tourplanner.backend.repository.TourLogRepository;
import com.tourplanner.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final TourRepository tourRepository;
    private final TourLogRepository tourLogRepository;
    private final UserRepository userRepository;

    public SearchService(TourRepository tourRepository, TourLogRepository tourLogRepository,
                          UserRepository userRepository) {
        this.tourRepository = tourRepository;
        this.tourLogRepository = tourLogRepository;
        this.userRepository = userRepository;
    }

    public List<TourResponse> searchTours(String query) {
        log.info("Searching tours with query: '{}'", query);
        User user = getCurrentUser();

        if (query == null || query.trim().isEmpty()) {
            return tourRepository.findByUserId(user.getId()).stream()
                    .map(this::mapTourToResponse)
                    .collect(Collectors.toList());
        }

        String searchTerm = "%" + query.toLowerCase() + "%";
        List<Tour> tours = tourRepository.searchTours(user.getId(), searchTerm);

        return tours.stream()
                .map(this::mapTourToResponse)
                .collect(Collectors.toList());
    }

    public List<TourLogResponse> searchTourLogs(String query) {
        log.info("Searching tour logs with query: '{}'", query);
        User user = getCurrentUser();

        if (query == null || query.trim().isEmpty()) {
            return tourLogRepository.findByTourUserId(user.getId()).stream()
                    .map(this::mapTourLogToResponse)
                    .collect(Collectors.toList());
        }

        String searchTerm = "%" + query.toLowerCase() + "%";
        List<TourLog> logs = tourLogRepository.searchTourLogs(user.getId(), searchTerm);

        return logs.stream()
                .map(this::mapTourLogToResponse)
                .collect(Collectors.toList());
    }

    public List<TourResponse> searchToursAdvanced(String name, String from, String to,
                                                   String transportType, Double minDistance,
                                                   Double maxDistance, Integer minPopularity) {
        log.info("Advanced search tours with filters");
        User user = getCurrentUser();

        List<Tour> tours = tourRepository.findByUserId(user.getId()).stream()
                .filter(tour -> name == null || tour.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(tour -> from == null || tour.getFrom().toLowerCase().contains(from.toLowerCase()))
                .filter(tour -> to == null || tour.getTo().toLowerCase().contains(to.toLowerCase()))
                .filter(tour -> transportType == null || tour.getTransportType().equalsIgnoreCase(transportType))
                .filter(tour -> minDistance == null || tour.getDistance() >= minDistance)
                .filter(tour -> maxDistance == null || tour.getDistance() <= maxDistance)
                .filter(tour -> minPopularity == null || tour.getPopularity() >= minPopularity)
                .collect(Collectors.toList());

        return tours.stream()
                .map(this::mapTourToResponse)
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private TourResponse mapTourToResponse(Tour tour) {
        return TourResponse.builder()
                .id(tour.getId())
                .name(tour.getName())
                .description(tour.getDescription())
                .from(tour.getFrom())
                .to(tour.getTo())
                .transportType(tour.getTransportType())
                .distance(tour.getDistance())
                .estimatedTimeMinutes(tour.getEstimatedTime() != null ? tour.getEstimatedTime().toMinutes() : null)
                .imagePath(tour.getImagePath())
                .popularity(tour.getPopularity())
                .childFriendliness(tour.getChildFriendliness())
                .build();
    }

    private TourLogResponse mapTourLogToResponse(TourLog tourLog) {
        return TourLogResponse.builder()
                .id(tourLog.getId())
                .dateTime(tourLog.getDateTime())
                .comment(tourLog.getComment())
                .difficulty(tourLog.getDifficulty())
                .totalDistance(tourLog.getTotalDistance())
                .totalTimeMinutes(tourLog.getTotalTime().toMinutes())
                .rating(tourLog.getRating())
                .tourId(tourLog.getTour().getId())
                .build();
    }
}
