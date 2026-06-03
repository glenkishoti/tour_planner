package com.tourplanner.backend.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;


@Converter
public class DurationMinutesConverter implements AttributeConverter<Duration, Long> {

    @Override
    public Long convertToDatabaseColumn(Duration duration) {
        return duration != null ? duration.toNanos() : null;
    }

    @Override
    public Duration convertToEntityAttribute(Long nanos) {
        return nanos != null ? Duration.ofNanos(nanos) : null;
    }
}
