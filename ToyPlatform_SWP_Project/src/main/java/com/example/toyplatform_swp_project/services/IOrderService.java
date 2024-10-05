package com.example.toyplatform_swp_project.services;

import com.example.toyplatform_swp_project.dto.OrderDto;

import java.util.List;

public interface IOrderService {
    OrderDto createOrder(OrderDto orderDto);
    List<OrderDto> getOrdersByUserId();
}
