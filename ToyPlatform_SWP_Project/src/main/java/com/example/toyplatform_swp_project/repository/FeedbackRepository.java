package com.example.toyplatform_swp_project.repository;

import com.example.toyplatform_swp_project.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByOrderOrderId(Long orderId);
    List<Feedback> findByToyToyId(Long toyId);

}
