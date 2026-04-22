package com.tourplanner.backend.service;

import com.tourplanner.backend.dto.StatisticsDTO;
import com.tourplanner.backend.entity.Tour;
import com.tourplanner.backend.entity.TourLog;
import com.tourplanner.backend.entity.User;
import com.tourplanner.backend.repository.TourRepository;
import com.tourplanner.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

    private static final Logger log = LoggerFactory.getLogger(StatisticsService.class);

    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    public StatisticsService(TourRepository tourRepository, UserRepository userRepository) {
        this.tourRepository = tourRepository;
        this.userRepository = userRepository;
    }

    public StatisticsDTO getUserStatistics() {
        log.info("Fetching user statistics");
        User user = getCurrentUser();

        List<Tour> tours = tourRepository.findByUserId(user.getId());

        StatisticsDTO stats = new StatisticsDTO();
        stats.setOverallStats(calculateOverallStats(tours));
        stats.setTourStats(calculateTourStats(tours));
        stats.setMonthlyStats(calculateMonthlyStats(tours));
        stats.setTransportTypeStats(calculateTransportTypeStats(tours));

        return stats;
    }

    public StatisticsDTO.TourStats getTourStatistics(Long tourId) {
        log.info("Fetching statistics for tour id: {}", tourId);
        User user = getCurrentUser();

        Tour tour = tourRepository.findByIdAndUserId(tourId, user.getId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Tour not found with id: " + tourId));

        return calculateSingleTourStats(tour);
    }

    private StatisticsDTO.OverallStats calculateOverallStats(List<Tour> tours) {
        int totalTours = tours.size();
        int totalLogs = tours.stream()
                .mapToInt(tour -> tour.getTourLogs().size())
                .sum();

        double totalDistance = tours.stream()
                .mapToDouble(Tour::getDistance)
                .sum();

        long totalTimeMinutes = tours.stream()
                .mapToLong(tour -> tour.getEstimatedTime() != null ? tour.getEstimatedTime().toMinutes() : 0)
                .sum();

        double averageTourDistance = totalTours > 0 ? totalDistance / totalTours : 0;
        long averageTourTimeMinutes = totalTours > 0 ? totalTimeMinutes / totalTours : 0;

        List<TourLog> allLogs = tours.stream()
                .flatMap(tour -> tour.getTourLogs().stream())
                .collect(Collectors.toList());

        double averageRating = allLogs.stream()
                .mapToInt(TourLog::getRating)
                .average()
                .orElse(0.0);

        int averageDifficulty = (int) Math.round(allLogs.stream()
                .mapToInt(TourLog::getDifficulty)
                .average()
                .orElse(0.0));

        StatisticsDTO.OverallStats stats = new StatisticsDTO.OverallStats();
        stats.setTotalTours(totalTours);
        stats.setTotalLogs(totalLogs);
        stats.setTotalDistance(Math.round(totalDistance * 100.0) / 100.0);
        stats.setTotalTimeMinutes(totalTimeMinutes);
        stats.setAverageTourDistance(Math.round(averageTourDistance * 100.0) / 100.0);
        stats.setAverageTourTimeMinutes(averageTourTimeMinutes);
        stats.setAverageRating(Math.round(averageRating * 100.0) / 100.0);
        stats.setAverageDifficulty(averageDifficulty);
        return stats;
    }

    private List<StatisticsDTO.TourStats> calculateTourStats(List<Tour> tours) {
        return tours.stream()
                .map(this::calculateSingleTourStats)
                .collect(Collectors.toList());
    }

    private StatisticsDTO.TourStats calculateSingleTourStats(Tour tour) {
        List<TourLog> logs = tour.getTourLogs();
        int logCount = logs.size();

        double totalDistance = logs.stream()
                .mapToDouble(TourLog::getTotalDistance)
                .sum();

        long totalTimeMinutes = logs.stream()
                .mapToLong(log -> log.getTotalTime() != null ? log.getTotalTime().toMinutes() : 0)
                .sum();

        double averageRating = logs.stream()
                .mapToInt(TourLog::getRating)
                .average()
                .orElse(0.0);

        int averageDifficulty = (int) Math.round(logs.stream()
                .mapToInt(TourLog::getDifficulty)
                .average()
                .orElse(0.0));

        Double childFriendliness = tour.getChildFriendliness();

        StatisticsDTO.TourStats stats = new StatisticsDTO.TourStats();
        stats.setTourId(tour.getId());
        stats.setTourName(tour.getName());
        stats.setLogCount(logCount);
        stats.setTotalDistance(Math.round(totalDistance * 100.0) / 100.0);
        stats.setTotalTimeMinutes(totalTimeMinutes);
        stats.setAverageRating(Math.round(averageRating * 100.0) / 100.0);
        stats.setAverageDifficulty(averageDifficulty);
        stats.setChildFriendliness(childFriendliness != null ? Math.round(childFriendliness * 100.0) / 100.0 : 0.0);
        return stats;
    }

    private StatisticsDTO.MonthlyStats calculateMonthlyStats(List<Tour> tours) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        Map<String, Integer> logsPerMonth = new HashMap<>();
        Map<String, Double> distancePerMonth = new HashMap<>();

        tours.stream()
                .flatMap(tour -> tour.getTourLogs().stream())
                .forEach(log -> {
                    String month = log.getDateTime().format(formatter);
                    logsPerMonth.merge(month, 1, Integer::sum);
                    distancePerMonth.merge(month, log.getTotalDistance(), Double::sum);
                });

        Map<String, Double> roundedDistance = distancePerMonth.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Math.round(e.getValue() * 100.0) / 100.0
                ));

        StatisticsDTO.MonthlyStats stats = new StatisticsDTO.MonthlyStats();
        stats.setLogsPerMonth(logsPerMonth);
        stats.setDistancePerMonth(roundedDistance);
        return stats;
    }

    private StatisticsDTO.TransportTypeStats calculateTransportTypeStats(List<Tour> tours) {
        Map<String, Integer> toursByTransportType = tours.stream()
                .collect(Collectors.groupingBy(
                        Tour::getTransportType,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        Map<String, Double> avgDistanceByTransportType = tours.stream()
                .collect(Collectors.groupingBy(
                        Tour::getTransportType,
                        Collectors.averagingDouble(Tour::getDistance)
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Math.round(e.getValue() * 100.0) / 100.0
                ));

        StatisticsDTO.TransportTypeStats stats = new StatisticsDTO.TransportTypeStats();
        stats.setToursByTransportType(toursByTransportType);
        stats.setAvgDistanceByTransportType(avgDistanceByTransportType);
        return stats;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
