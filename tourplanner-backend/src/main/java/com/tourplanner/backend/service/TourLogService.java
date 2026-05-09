package com.tourplanner.backend.service;

import com.tourplanner.backend.dto.TourLogRequest;
import com.tourplanner.backend.dto.TourLogResponse;
import com.tourplanner.backend.entity.Tour;
import com.tourplanner.backend.entity.TourLog;
import com.tourplanner.backend.entity.User;
import com.tourplanner.backend.repository.TourLogRepository;
import com.tourplanner.backend.repository.TourRepository;
import com.tourplanner.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TourLogService {

    private static final Logger log = LoggerFactory.getLogger(TourLogService.class);

    private final TourLogRepository tourLogRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    public TourLogService(TourLogRepository tourLogRepository, TourRepository tourRepository, UserRepository userRepository) {
        this.tourLogRepository = tourLogRepository;
        this.tourRepository = tourRepository;
        this.userRepository = userRepository;
    }

    public TourLogResponse createTourLog(Long tourId, TourLogRequest request) {
        log.info("Creating new tour log for tour id: {}", tourId);

        User user = getCurrentUser();
        Tour tour = tourRepository.findByIdAndUserId(tourId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tour not found with id: " + tourId));

        TourLog tourLog = new TourLog();
        mapRequestToEntity(request, tourLog);
        tourLog.setTour(tour);

        TourLog savedLog = tourLogRepository.save(tourLog);
        log.info("Tour log created successfully with id: {}", savedLog.getId());

        return mapToResponse(savedLog);
    }

    @Transactional(readOnly = true)
    public List<TourLogResponse> getAllTourLogsForTour(Long tourId) {
        User user = getCurrentUser();
        log.debug("Fetching all tour logs for tour id: {} and user: {}", tourId, user.getUsername());

        if (!tourRepository.existsByIdAndUserId(tourId, user.getId())) {
            throw new EntityNotFoundException("Tour not found with id: " + tourId);
        }

        return tourLogRepository.findByTourIdAndTourUserId(tourId, user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TourLogResponse getTourLogById(Long id) {
        User user = getCurrentUser();
        log.debug("Fetching tour log with id: {} for user: {}", id, user.getUsername());

        TourLog tourLog = tourLogRepository.findByIdAndTourUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tour log not found with id: " + id));

        return mapToResponse(tourLog);
    }

    public TourLogResponse updateTourLog(Long id, TourLogRequest request) {
        User user = getCurrentUser();
        log.info("Updating tour log with id: {} for user: {}", id, user.getUsername());

        TourLog tourLog = tourLogRepository.findByIdAndTourUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tour log not found with id: " + id));

        mapRequestToEntity(request, tourLog);

        TourLog updatedLog = tourLogRepository.save(tourLog);
        log.info("Tour log updated successfully with id: {}", updatedLog.getId());

        return mapToResponse(updatedLog);
    }

    public void deleteTourLog(Long id) {
        User user = getCurrentUser();
        log.info("Deleting tour log with id: {} for user: {}", id, user.getUsername());

        TourLog tourLog = tourLogRepository.findByIdAndTourUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tour log not found with id: " + id));

        tourLogRepository.delete(tourLog);
        log.info("Tour log deleted successfully with id: {}", id);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private void mapRequestToEntity(TourLogRequest request, TourLog tourLog) {
        tourLog.setDateTime(request.getDateTime());
        tourLog.setComment(request.getComment());
        tourLog.setDifficulty(request.getDifficulty());
        tourLog.setTotalDistance(request.getTotalDistance());
        tourLog.setTotalTime(Duration.ofMinutes(request.getTotalTimeMinutes()));
        tourLog.setRating(request.getRating());
    }

    private TourLogResponse mapToResponse(TourLog tourLog) {
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
