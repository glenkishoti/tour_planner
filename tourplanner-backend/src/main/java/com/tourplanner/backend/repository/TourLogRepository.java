package com.tourplanner.backend.repository;

import com.tourplanner.backend.entity.TourLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<TourLog> findByTourUserId(Long userId);

    @Query("SELECT tl FROM TourLog tl WHERE tl.tour.user.id = :userId AND (" +
           "LOWER(tl.comment) LIKE LOWER(:searchTerm) OR " +
           "LOWER(tl.tour.name) LIKE LOWER(:searchTerm))" +
           "ORDER BY tl.dateTime DESC")
    List<TourLog> searchTourLogs(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);
}
