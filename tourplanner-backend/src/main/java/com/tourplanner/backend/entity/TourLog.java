package com.tourplanner.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "tour_logs")
@Getter
@Setter
@NoArgsConstructor
public class TourLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Date and time is required")
    @Column(nullable = false, name = "date_time")
    private LocalDateTime dateTime;

    @NotBlank(message = "Comment is required")
    @Column(nullable = false, length = 1000)
    private String comment;

    @NotNull(message = "Difficulty is required")
    @Min(value = 1, message = "Difficulty must be between 1 and 10")
    @Max(value = 10, message = "Difficulty must be between 1 and 10")
    @Column(nullable = false)
    private Integer difficulty;

    @NotNull(message = "Total distance is required")
    @Positive(message = "Total distance must be positive")
    @Column(nullable = false, name = "total_distance")
    private Double totalDistance;

    @NotNull(message = "Total time is required")
    @Column(nullable = false, name = "total_time")
    private Duration totalTime;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    @Column(nullable = false)
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;
}
