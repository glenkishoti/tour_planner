package com.tourplanner.backend.repository;

import com.tourplanner.backend.entity.TourLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourLogRepository extends JpaRepository<TourLog, Long> {
    List<TourLog> findByTourId(Long tourId);
    List<TourLog> findByTourIdAndTourUserId(Long tourId, Long userId);
    Optional<TourLog> findByIdAndTourUserId(Long id, Long userId);
    boolean existsByIdAndTourUserId(Long id, Long userId);
    long countByTourId(Long tourId);
}
