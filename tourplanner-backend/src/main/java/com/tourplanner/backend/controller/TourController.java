package com.tourplanner.backend.controller;

import com.tourplanner.backend.dto.TourRequest;
import com.tourplanner.backend.dto.TourResponse;
import com.tourplanner.backend.service.TourService;
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
@RequestMapping("/api/tours")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class TourController {

    private final TourService tourService;

    @PostMapping
    public ResponseEntity<TourResponse> createTour(@Valid @RequestBody TourRequest request) {
        log.info("POST /api/tours - Creating new tour");
        TourResponse response = tourService.createTour(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TourResponse>> getAllTours() {
        log.info("GET /api/tours - Fetching all tours for current user");
        List<TourResponse> tours = tourService.getAllToursForCurrentUser();
        return ResponseEntity.ok(tours);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourResponse> getTourById(@PathVariable Long id) {
        log.info("GET /api/tours/{} - Fetching tour by id", id);
        TourResponse tour = tourService.getTourById(id);
        return ResponseEntity.ok(tour);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourResponse> updateTour(
            @PathVariable Long id,
            @Valid @RequestBody TourRequest request) {
        log.info("PUT /api/tours/{} - Updating tour", id);
        TourResponse response = tourService.updateTour(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTour(@PathVariable Long id) {
        log.info("DELETE /api/tours/{} - Deleting tour", id);
        tourService.deleteTour(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Tour deleted successfully");
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
