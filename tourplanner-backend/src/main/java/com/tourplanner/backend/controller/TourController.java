package com.tourplanner.backend.controller;

import com.tourplanner.backend.dto.TourRequest;
import com.tourplanner.backend.dto.TourResponse;
import com.tourplanner.backend.service.FileStorageService;
import com.tourplanner.backend.service.TourService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tours")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class TourController {

    private static final Logger log = LoggerFactory.getLogger(TourController.class);
    private final TourService tourService;
    private final FileStorageService fileStorageService;

    public TourController(TourService tourService, FileStorageService fileStorageService) {
        this.tourService = tourService;
        this.fileStorageService = fileStorageService;
    }

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

    @PostMapping("/{id}/image")
    public ResponseEntity<Map<String, String>> uploadTourImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        log.info("POST /api/tours/{}/image - Uploading tour image", id);

        String filePath = fileStorageService.storeFile(file);
        tourService.updateTourImage(id, filePath);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Image uploaded successfully");
        response.put("imagePath", filePath);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getTourImage(@PathVariable Long id) {
        log.info("GET /api/tours/{}/image - Getting tour image", id);

        TourResponse tour = tourService.getTourById(id);
        String imagePath = tour.getImagePath();

        if (imagePath == null || imagePath.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageBytes = fileStorageService.loadFile(imagePath);

        if (imageBytes == null) {
            return ResponseEntity.notFound().build();
        }

        MediaType contentType = MediaType.parseMediaType(fileStorageService.getContentType(imagePath));
        return ResponseEntity.ok()
                .contentType(contentType)
                .body(imageBytes);
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
