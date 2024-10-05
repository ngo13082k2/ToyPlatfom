package com.example.toyplatform_swp_project.services;

import com.example.toyplatform_swp_project.dto.FeedbackDto;

import java.util.List;

public interface IFeedbackService {
    FeedbackDto createFeedback(FeedbackDto feedbackDto);
    List<FeedbackDto> getFeedbacksByToy(Long toyId);
    List<FeedbackDto> getFeedbacksByOrder(Long orderId);
}
