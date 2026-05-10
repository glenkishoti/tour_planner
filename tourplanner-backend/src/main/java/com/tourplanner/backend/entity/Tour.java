package com.tourplanner.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tours")
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tour name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 1000)
    private String description;

    @NotBlank(message = "Start location is required")
    @Column(nullable = false, name = "start_location")
    private String from;

    @NotBlank(message = "End location is required")
    @Column(nullable = false, name = "end_location")
    private String to;

    @NotBlank(message = "Transport type is required")
    @Column(nullable = false, name = "transport_type")
    private String transportType;

    @NotNull(message = "Distance is required")
    @Positive(message = "Distance must be positive")
    @Column(nullable = false)
    private Double distance;

    @NotNull(message = "Estimated time is required")
    @Column(nullable = false, name = "estimated_time")
    private Duration estimatedTime;

    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourLog> tourLogs = new ArrayList<>();

    public Tour() {}

    @Transient
    public Integer getPopularity() {
        return tourLogs != null ? tourLogs.size() : 0;
    }

    @Transient
    public Double getChildFriendliness() {
        if (tourLogs == null || tourLogs.isEmpty()) {
            return null;
        }

        double avgDifficulty = tourLogs.stream()
                .mapToInt(TourLog::getDifficulty)
                .average()
                .orElse(5.0);

        double avgTime = tourLogs.stream()
                .mapToLong(log -> log.getTotalTime().toMinutes())
                .average()
                .orElse(120.0);

        double avgDistance = tourLogs.stream()
                .mapToDouble(TourLog::getTotalDistance)
                .average()
                .orElse(10.0);

        double normalizedDifficulty = (10 - avgDifficulty) / 9.0;
        double normalizedTime = Math.max(0, 1 - (avgTime / 300.0));
        double normalizedDistance = Math.max(0, 1 - (avgDistance / 20.0));

        return (normalizedDifficulty * 0.4 + normalizedTime * 0.3 + normalizedDistance * 0.3) * 100;
    }
}
