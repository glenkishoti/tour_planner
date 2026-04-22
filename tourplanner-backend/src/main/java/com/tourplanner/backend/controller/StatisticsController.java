package com.tourplanner.backend.controller;

import com.tourplanner.backend.dto.StatisticsDTO;
import com.tourplanner.backend.service.StatisticsService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class StatisticsController {

    private static final Logger log = LoggerFactory.getLogger(StatisticsController.class);
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public ResponseEntity<StatisticsDTO> getUserStatistics() {
        log.info("GET /api/statistics - Fetching user statistics");
        StatisticsDTO statistics = statisticsService.getUserStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<StatisticsDTO.TourStats> getTourStatistics(@PathVariable Long tourId) {
        log.info("GET /api/statistics/tour/{} - Fetching tour statistics", tourId);
        StatisticsDTO.TourStats statistics = statisticsService.getTourStatistics(tourId);
        return ResponseEntity.ok(statistics);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        log.error("Statistics error: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Failed to fetch statistics: " + ex.getMessage());
        return ResponseEntity.status(500).body(error);
    }
}
