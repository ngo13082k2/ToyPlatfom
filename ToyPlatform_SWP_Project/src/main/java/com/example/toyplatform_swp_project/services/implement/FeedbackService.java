package com.example.toyplatform_swp_project.services.implement;

import com.example.toyplatform_swp_project.dto.FeedbackDto;
import com.example.toyplatform_swp_project.model.Feedback;
import com.example.toyplatform_swp_project.model.Order;
import com.example.toyplatform_swp_project.model.Toy;
import com.example.toyplatform_swp_project.repository.FeedbackRepository;
import com.example.toyplatform_swp_project.repository.OrderRepository;
import com.example.toyplatform_swp_project.repository.ToyRepository;
import com.example.toyplatform_swp_project.services.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FeedbackService implements IFeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ToyRepository toyRepository;

    public FeedbackDto createFeedback(FeedbackDto feedbackDto) {
        Feedback feedback = new Feedback();
        feedback.setRating(feedbackDto.getRating());
        feedback.setComment(feedbackDto.getComment());
        feedback.setFeedbackDate(LocalDate.now());

        Order order = orderRepository.findById(feedbackDto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + feedbackDto.getOrderId()));
        feedback.setOrder(order);

        Toy toy = toyRepository.findById(feedbackDto.getToyId())
                .orElseThrow(() -> new RuntimeException("Toy not found with id: " + feedbackDto.getToyId()));
        feedback.setToy(toy);

        Feedback savedFeedback = feedbackRepository.save(feedback);
        return mapToDto(savedFeedback);
    }
    public List<FeedbackDto> getFeedbacksByToy(Long toyId) {
        List<Feedback> feedbacks = feedbackRepository.findByToyToyId(toyId);
        return feedbacks.stream().map(this::mapToDto).toList();
    }

    public List<FeedbackDto> getFeedbacksByOrder(Long orderId) {
        List<Feedback> feedbacks = feedbackRepository.findByOrderOrderId(orderId);
        return feedbacks.stream().map(this::mapToDto).toList();
    }

    private FeedbackDto mapToDto(Feedback feedback) {
        FeedbackDto dto = new FeedbackDto();
        dto.setFeedbackId(feedback.getFeedbackId());
        dto.setOrderId(feedback.getOrder().getOrderId());
        dto.setToyId(feedback.getToy().getToyId());
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        dto.setFeedbackDate(feedback.getFeedbackDate());
        return dto;
    }

}
