package com.tourplanner.backend.dto;

public class TourResponse {
    private Long id;
    private String name;
    private String description;
    private String from;
    private String to;
    private String transportType;
    private Double distance;
    private Long estimatedTimeMinutes;
    private String imagePath;
    private Integer popularity;
    private Double childFriendliness;

    public TourResponse() {}

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

    public Long getEstimatedTimeMinutes() { return estimatedTimeMinutes; }
    public void setEstimatedTimeMinutes(Long estimatedTimeMinutes) { this.estimatedTimeMinutes = estimatedTimeMinutes; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Integer getPopularity() { return popularity; }
    public void setPopularity(Integer popularity) { this.popularity = popularity; }

    public Double getChildFriendliness() { return childFriendliness; }
    public void setChildFriendliness(Double childFriendliness) { this.childFriendliness = childFriendliness; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TourResponse response = new TourResponse();

        public Builder id(Long id) { response.setId(id); return this; }
        public Builder name(String name) { response.setName(name); return this; }
        public Builder description(String description) { response.setDescription(description); return this; }
        public Builder from(String from) { response.setFrom(from); return this; }
        public Builder to(String to) { response.setTo(to); return this; }
        public Builder transportType(String transportType) { response.setTransportType(transportType); return this; }
        public Builder distance(Double distance) { response.setDistance(distance); return this; }
        public Builder estimatedTimeMinutes(Long estimatedTimeMinutes) { response.setEstimatedTimeMinutes(estimatedTimeMinutes); return this; }
        public Builder imagePath(String imagePath) { response.setImagePath(imagePath); return this; }
        public Builder popularity(Integer popularity) { response.setPopularity(popularity); return this; }
        public Builder childFriendliness(Double childFriendliness) { response.setChildFriendliness(childFriendliness); return this; }

        public TourResponse build() { return response; }
    }
}
