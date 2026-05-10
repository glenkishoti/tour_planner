package com.tourplanner.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class StatisticsDTO {

    private OverallStats overallStats;
    private List<TourStats> tourStats;
    private MonthlyStats monthlyStats;
    private TransportTypeStats transportTypeStats;

    @Setter
    @Getter
    public static class OverallStats {
        private int totalTours;
        private int totalLogs;
        private double totalDistance;
        private long totalTimeMinutes;
        private double averageTourDistance;
        private long averageTourTimeMinutes;
        private double averageRating;
        private int averageDifficulty;

    }

    @Setter
    @Getter
    public static class TourStats {
        private Long tourId;
        private String tourName;
        private int logCount;
        private double totalDistance;
        private long totalTimeMinutes;
        private double averageRating;
        private int averageDifficulty;
        private double childFriendliness;

    }

    @Setter
    @Getter
    public static class MonthlyStats {
        private Map<String, Integer> logsPerMonth;
        private Map<String, Double> distancePerMonth;

    }

    @Setter
    @Getter
    public static class TransportTypeStats {
        private Map<String, Integer> toursByTransportType;
        private Map<String, Double> avgDistanceByTransportType;

    }
}
