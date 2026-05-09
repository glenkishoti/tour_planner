package com.tourplanner.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getTransportType() { return transportType; }
    public void setTransportType(String transportType) { this.transportType = transportType; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public Duration getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(Duration estimatedTime) { this.estimatedTime = estimatedTime; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<TourLog> getTourLogs() { return tourLogs; }
    public void setTourLogs(List<TourLog> tourLogs) { this.tourLogs = tourLogs; }

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
                .mapToInt(log -> log.getDifficulty())
                .average()
                .orElse(5.0);

        double avgTime = tourLogs.stream()
                .mapToLong(log -> log.getTotalTime().toMinutes())
                .average()
                .orElse(120.0);

        double avgDistance = tourLogs.stream()
                .mapToDouble(log -> log.getTotalDistance())
                .average()
                .orElse(10.0);

        double normalizedDifficulty = (10 - avgDifficulty) / 9.0;
        double normalizedTime = Math.max(0, 1 - (avgTime / 300.0));
        double normalizedDistance = Math.max(0, 1 - (avgDistance / 20.0));

        return (normalizedDifficulty * 0.4 + normalizedTime * 0.3 + normalizedDistance * 0.3) * 100;
    }
}
