package com.tourplanner.backend.controller;

import com.tourplanner.backend.service.ImportExportService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/import-export")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class ImportExportController {

    private static final Logger log = LoggerFactory.getLogger(ImportExportController.class);
    private final ImportExportService importExportService;

    public ImportExportController(ImportExportService importExportService) {
        this.importExportService = importExportService;
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportAllTours() {
        log.info("GET /api/import-export/export - Exporting all tours");
        String jsonData = importExportService.exportAllTours();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"tours-export.json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonData);
    }

    @GetMapping("/export/{tourId}")
    public ResponseEntity<String> exportSingleTour(@PathVariable Long tourId) {
        log.info("GET /api/import-export/export/{} - Exporting single tour", tourId);
        String jsonData = importExportService.exportTour(tourId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"tour-" + tourId + "-export.json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonData);
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importTours(@RequestParam("file") MultipartFile file) {
        log.info("POST /api/import-export/import - Importing tours from file: {}", file.getOriginalFilename());

        try {
            String jsonData = new String(file.getBytes(), StandardCharsets.UTF_8);
            int importedCount = importExportService.importTours(jsonData);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tours imported successfully");
            response.put("importedCount", importedCount);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error reading import file: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to read import file");
            return ResponseEntity.status(400).body((Map) error);
        }
    }

    @PostMapping("/import-json")
    public ResponseEntity<Map<String, Object>> importToursJson(@RequestBody String jsonData) {
        log.info("POST /api/import-export/import-json - Importing tours from JSON body");

        int importedCount = importExportService.importTours(jsonData);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Tours imported successfully");
        response.put("importedCount", importedCount);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        log.error("Import/Export error: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(500).body(error);
    }
}
