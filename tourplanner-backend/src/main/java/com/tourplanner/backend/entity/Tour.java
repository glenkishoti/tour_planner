package com.tourplanner.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
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
}
