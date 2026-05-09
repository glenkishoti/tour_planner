package com.tourplanner.backend.controller;

import com.tourplanner.backend.dto.TourLogResponse;
import com.tourplanner.backend.dto.TourResponse;
import com.tourplanner.backend.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/tours")
    public ResponseEntity<List<TourResponse>> searchTours(@RequestParam(required = false) String query) {
        log.info("GET /api/search/tours - Searching tours with query: '{}'", query);
        List<TourResponse> tours = searchService.searchTours(query);
        return ResponseEntity.ok(tours);
    }

    @GetMapping("/tour-logs")
    public ResponseEntity<List<TourLogResponse>> searchTourLogs(@RequestParam(required = false) String query) {
        log.info("GET /api/search/tour-logs - Searching tour logs with query: '{}'", query);
        List<TourLogResponse> logs = searchService.searchTourLogs(query);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/tours/advanced")
    public ResponseEntity<List<TourResponse>> searchToursAdvanced(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String transportType,
            @RequestParam(required = false) Double minDistance,
            @RequestParam(required = false) Double maxDistance,
            @RequestParam(required = false) Integer minPopularity) {
        log.info("GET /api/search/tours/advanced - Advanced search with filters");
        List<TourResponse> tours = searchService.searchToursAdvanced(
                name, from, to, transportType, minDistance, maxDistance, minPopularity);
        return ResponseEntity.ok(tours);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        log.error("Search error: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Search failed: " + ex.getMessage());
        return ResponseEntity.status(500).body(error);
    }
}
