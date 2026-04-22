package com.tourplanner.backend.repository;

import com.tourplanner.backend.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    List<Tour> findByUserId(Long userId);
    Optional<Tour> findByIdAndUserId(Long id, Long userId);
    boolean existsByIdAndUserId(Long id, Long userId);

    @Query("SELECT t FROM Tour t WHERE t.user.id = :userId AND (" +
           "LOWER(t.name) LIKE LOWER(:searchTerm) OR " +
           "LOWER(COALESCE(t.description, '')) LIKE LOWER(:searchTerm) OR " +
           "LOWER(t.from) LIKE LOWER(:searchTerm) OR " +
           "LOWER(t.to) LIKE LOWER(:searchTerm) OR " +
           "LOWER(t.transportType) LIKE LOWER(:searchTerm))" +
           "ORDER BY t.name ASC")
    List<Tour> searchTours(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);
}
