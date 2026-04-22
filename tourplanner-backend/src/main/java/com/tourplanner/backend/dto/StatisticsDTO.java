package com.tourplanner.backend.dto;

import java.util.List;
import java.util.Map;

public class StatisticsDTO {

    private OverallStats overallStats;
    private List<TourStats> tourStats;
    private MonthlyStats monthlyStats;
    private TransportTypeStats transportTypeStats;

    public OverallStats getOverallStats() { return overallStats; }
    public void setOverallStats(OverallStats overallStats) { this.overallStats = overallStats; }

    public List<TourStats> getTourStats() { return tourStats; }
    public void setTourStats(List<TourStats> tourStats) { this.tourStats = tourStats; }

    public MonthlyStats getMonthlyStats() { return monthlyStats; }
    public void setMonthlyStats(MonthlyStats monthlyStats) { this.monthlyStats = monthlyStats; }

    public TransportTypeStats getTransportTypeStats() { return transportTypeStats; }
    public void setTransportTypeStats(TransportTypeStats transportTypeStats) { this.transportTypeStats = transportTypeStats; }

    public static class OverallStats {
        private int totalTours;
        private int totalLogs;
        private double totalDistance;
        private long totalTimeMinutes;
        private double averageTourDistance;
        private long averageTourTimeMinutes;
        private double averageRating;
        private int averageDifficulty;

        public int getTotalTours() { return totalTours; }
        public void setTotalTours(int totalTours) { this.totalTours = totalTours; }

        public int getTotalLogs() { return totalLogs; }
        public void setTotalLogs(int totalLogs) { this.totalLogs = totalLogs; }

        public double getTotalDistance() { return totalDistance; }
        public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

        public long getTotalTimeMinutes() { return totalTimeMinutes; }
        public void setTotalTimeMinutes(long totalTimeMinutes) { this.totalTimeMinutes = totalTimeMinutes; }

        public double getAverageTourDistance() { return averageTourDistance; }
        public void setAverageTourDistance(double averageTourDistance) { this.averageTourDistance = averageTourDistance; }

        public long getAverageTourTimeMinutes() { return averageTourTimeMinutes; }
        public void setAverageTourTimeMinutes(long averageTourTimeMinutes) { this.averageTourTimeMinutes = averageTourTimeMinutes; }

        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

        public int getAverageDifficulty() { return averageDifficulty; }
        public void setAverageDifficulty(int averageDifficulty) { this.averageDifficulty = averageDifficulty; }
    }

    public static class TourStats {
        private Long tourId;
        private String tourName;
        private int logCount;
        private double totalDistance;
        private long totalTimeMinutes;
        private double averageRating;
        private int averageDifficulty;
        private double childFriendliness;

        public Long getTourId() { return tourId; }
        public void setTourId(Long tourId) { this.tourId = tourId; }

        public String getTourName() { return tourName; }
        public void setTourName(String tourName) { this.tourName = tourName; }

        public int getLogCount() { return logCount; }
        public void setLogCount(int logCount) { this.logCount = logCount; }

        public double getTotalDistance() { return totalDistance; }
        public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

        public long getTotalTimeMinutes() { return totalTimeMinutes; }
        public void setTotalTimeMinutes(long totalTimeMinutes) { this.totalTimeMinutes = totalTimeMinutes; }

        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

        public int getAverageDifficulty() { return averageDifficulty; }
        public void setAverageDifficulty(int averageDifficulty) { this.averageDifficulty = averageDifficulty; }

        public double getChildFriendliness() { return childFriendliness; }
        public void setChildFriendliness(double childFriendliness) { this.childFriendliness = childFriendliness; }
    }

    public static class MonthlyStats {
        private Map<String, Integer> logsPerMonth;
        private Map<String, Double> distancePerMonth;

        public Map<String, Integer> getLogsPerMonth() { return logsPerMonth; }
        public void setLogsPerMonth(Map<String, Integer> logsPerMonth) { this.logsPerMonth = logsPerMonth; }

        public Map<String, Double> getDistancePerMonth() { return distancePerMonth; }
        public void setDistancePerMonth(Map<String, Double> distancePerMonth) { this.distancePerMonth = distancePerMonth; }
    }

    public static class TransportTypeStats {
        private Map<String, Integer> toursByTransportType;
        private Map<String, Double> avgDistanceByTransportType;

        public Map<String, Integer> getToursByTransportType() { return toursByTransportType; }
        public void setToursByTransportType(Map<String, Integer> toursByTransportType) { this.toursByTransportType = toursByTransportType; }

        public Map<String, Double> getAvgDistanceByTransportType() { return avgDistanceByTransportType; }
        public void setAvgDistanceByTransportType(Map<String, Double> avgDistanceByTransportType) { this.avgDistanceByTransportType = avgDistanceByTransportType; }
    }
}
