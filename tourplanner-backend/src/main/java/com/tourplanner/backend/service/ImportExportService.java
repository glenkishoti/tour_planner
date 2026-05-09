package com.tourplanner.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tourplanner.backend.dto.ImportExportDTO;
import com.tourplanner.backend.entity.Tour;
import com.tourplanner.backend.entity.TourLog;
import com.tourplanner.backend.entity.User;
import com.tourplanner.backend.repository.TourRepository;
import com.tourplanner.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ImportExportService {

    private static final Logger log = LoggerFactory.getLogger(ImportExportService.class);

    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public ImportExportService(TourRepository tourRepository, UserRepository userRepository) {
        this.tourRepository = tourRepository;
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Transactional(readOnly = true)
    public String exportAllTours() {
        log.info("Exporting all tours for current user");
        User user = getCurrentUser();

        List<Tour> tours = tourRepository.findByUserId(user.getId());

        List<ImportExportDTO.TourExportData> tourExportDataList = tours.stream()
                .map(this::mapToTourExportData)
                .collect(Collectors.toList());

        ImportExportDTO export = new ImportExportDTO();
        export.setExportVersion("1.0");
        export.setExportDate(LocalDateTime.now());
        export.setTours(tourExportDataList);

        try {
            return objectMapper.writeValueAsString(export);
        } catch (IOException e) {
            log.error("Error exporting tours: {}", e.getMessage());
            throw new RuntimeException("Failed to export tours", e);
        }
    }

    @Transactional(readOnly = true)
    public String exportTour(Long tourId) {
        log.info("Exporting tour with id: {}", tourId);
        User user = getCurrentUser();

        Tour tour = tourRepository.findByIdAndUserId(tourId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tour not found with id: " + tourId));

        ImportExportDTO.TourExportData tourData = mapToTourExportData(tour);

        ImportExportDTO export = new ImportExportDTO();
        export.setExportVersion("1.0");
        export.setExportDate(LocalDateTime.now());
        export.setTours(List.of(tourData));

        try {
            return objectMapper.writeValueAsString(export);
        } catch (IOException e) {
            log.error("Error exporting tour: {}", e.getMessage());
            throw new RuntimeException("Failed to export tour", e);
        }
    }

    public int importTours(String jsonData) {
        log.info("Importing tours from JSON data");
        User user = getCurrentUser();

        try {
            ImportExportDTO importData = objectMapper.readValue(jsonData, ImportExportDTO.class);

            if (importData.getTours() == null || importData.getTours().isEmpty()) {
                log.warn("No tours found in import data");
                return 0;
            }

            int importedCount = 0;
            for (ImportExportDTO.TourExportData tourData : importData.getTours()) {
                Tour tour = new Tour();
                tour.setName(tourData.getName());
                tour.setDescription(tourData.getDescription());
                tour.setFrom(tourData.getFrom());
                tour.setTo(tourData.getTo());
                tour.setTransportType(tourData.getTransportType());
                tour.setDistance(tourData.getDistance());
                tour.setEstimatedTime(tourData.getEstimatedTimeMinutes() != null ?
                        Duration.ofMinutes(tourData.getEstimatedTimeMinutes()) : null);
                tour.setImagePath(tourData.getImagePath());
                tour.setUser(user);

                Tour savedTour = tourRepository.save(tour);

                if (tourData.getLogs() != null) {
                    for (ImportExportDTO.TourLogExportData logData : tourData.getLogs()) {
                        TourLog tourLog = new TourLog();
                        tourLog.setDateTime(logData.getDateTime());
                        tourLog.setComment(logData.getComment());
                        tourLog.setDifficulty(logData.getDifficulty());
                        tourLog.setTotalDistance(logData.getTotalDistance());
                        tourLog.setTotalTime(logData.getTotalTimeMinutes() != null ?
                                Duration.ofMinutes(logData.getTotalTimeMinutes()) : null);
                        tourLog.setRating(logData.getRating());
                        tourLog.setTour(savedTour);

                        savedTour.getTourLogs().add(tourLog);
                    }
                }

                importedCount++;
            }

            log.info("Successfully imported {} tours", importedCount);
            return importedCount;
        } catch (IOException e) {
            log.error("Error importing tours: {}", e.getMessage());
            throw new RuntimeException("Failed to import tours", e);
        }
    }

    private ImportExportDTO.TourExportData mapToTourExportData(Tour tour) {
        ImportExportDTO.TourExportData data = new ImportExportDTO.TourExportData();
        data.setName(tour.getName());
        data.setDescription(tour.getDescription());
        data.setFrom(tour.getFrom());
        data.setTo(tour.getTo());
        data.setTransportType(tour.getTransportType());
        data.setDistance(tour.getDistance());
        data.setEstimatedTimeMinutes(tour.getEstimatedTime() != null ? tour.getEstimatedTime().toMinutes() : null);
        data.setImagePath(tour.getImagePath());
        
        if (tour.getTourLogs() != null) {
            List<ImportExportDTO.TourLogExportData> logs = tour.getTourLogs().stream()
                    .map(log -> {
                        ImportExportDTO.TourLogExportData logData = new ImportExportDTO.TourLogExportData();
                        logData.setDateTime(log.getDateTime());
                        logData.setComment(log.getComment());
                        logData.setDifficulty(log.getDifficulty());
                        logData.setTotalDistance(log.getTotalDistance());
                        logData.setTotalTimeMinutes(log.getTotalTime() != null ? log.getTotalTime().toMinutes() : null);
                        logData.setRating(log.getRating());
                        return logData;
                    })
                    .collect(Collectors.toList());
            data.setLogs(logs);
        }
        
        return data;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
