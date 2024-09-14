package com.example.toyplatform_swp_project.services;

import com.example.toyplatform_swp_project.dto.OrderDto;

public interface IOrderService {
    OrderDto createOrder(OrderDto orderDto);
}
