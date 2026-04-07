package com.tourplanner.backend.controller;

import com.tourplanner.backend.dto.TourLogRequest;
import com.tourplanner.backend.dto.TourLogResponse;
import com.tourplanner.backend.service.TourLogService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tours/{tourId}/logs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class TourLogController {

    private final TourLogService tourLogService;

    @PostMapping
    public ResponseEntity<TourLogResponse> createTourLog(
            @PathVariable Long tourId,
            @Valid @RequestBody TourLogRequest request) {
        log.info("POST /api/tours/{}/logs - Creating new tour log", tourId);
        TourLogResponse response = tourLogService.createTourLog(tourId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TourLogResponse>> getAllTourLogs(@PathVariable Long tourId) {
        log.info("GET /api/tours/{}/logs - Fetching all tour logs", tourId);
        List<TourLogResponse> logs = tourLogService.getAllTourLogsForTour(tourId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourLogResponse> getTourLogById(
            @PathVariable Long tourId,
            @PathVariable Long id) {
        log.info("GET /api/tours/{}/logs/{} - Fetching tour log", tourId, id);
        TourLogResponse log = tourLogService.getTourLogById(id);
        return ResponseEntity.ok(log);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourLogResponse> updateTourLog(
            @PathVariable Long tourId,
            @PathVariable Long id,
            @Valid @RequestBody TourLogRequest request) {
        log.info("PUT /api/tours/{}/logs/{} - Updating tour log", tourId, id);
        TourLogResponse response = tourLogService.updateTourLog(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTourLog(
            @PathVariable Long tourId,
            @PathVariable Long id) {
        log.info("DELETE /api/tours/{}/logs/{} - Deleting tour log", tourId, id);
        tourLogService.deleteTourLog(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Tour log deleted successfully");
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
