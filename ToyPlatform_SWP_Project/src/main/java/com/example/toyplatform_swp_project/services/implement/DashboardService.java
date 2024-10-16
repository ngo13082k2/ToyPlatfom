package com.example.toyplatform_swp_project.services.implement;

import com.example.toyplatform_swp_project.repository.OrderRepository;
import com.example.toyplatform_swp_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Long getTotalUsers() {
        return userRepository.count();
    }

    public Long getTotalOrders() {
        return orderRepository.count();
    }

    public Double getTotalRevenue() {
        return orderRepository.getTotalRevenue();
    }
}
