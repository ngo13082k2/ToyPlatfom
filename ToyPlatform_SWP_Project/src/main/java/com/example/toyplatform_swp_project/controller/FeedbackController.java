package com.example.toyplatform_swp_project.controller;

import com.example.toyplatform_swp_project.dto.FeedbackDto;
import com.example.toyplatform_swp_project.services.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private IFeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackDto> createFeedback(@RequestBody FeedbackDto feedbackDto) {
        FeedbackDto createdFeedback = feedbackService.createFeedback(feedbackDto);
        return ResponseEntity.ok(createdFeedback);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<FeedbackDto>> getFeedbacksByOrder(@PathVariable Long orderId) {
        List<FeedbackDto> feedbacks = feedbackService.getFeedbacksByOrder(orderId);
        return ResponseEntity.ok(feedbacks);
    }
    @GetMapping("/toy/{toyId}")
    public ResponseEntity<List<FeedbackDto>> getFeedbacksByToy(@PathVariable Long toyId) {
        List<FeedbackDto> feedbacks = feedbackService.getFeedbacksByToy(toyId);
        return ResponseEntity.ok(feedbacks);
    }
}
