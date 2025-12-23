package com.homeqadam.backend.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByOrderId(Long orderId);

    Optional<Review> findByOrderId(Long orderId);

    List<Review> findByTechnicianIdOrderByCreatedAtDesc(Long technicianId);

    List<Review> findAllByOrderByCreatedAtDesc();
}
