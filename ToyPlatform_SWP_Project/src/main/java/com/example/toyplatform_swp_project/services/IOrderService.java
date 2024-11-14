package com.example.toyplatform_swp_project.services;

import com.example.toyplatform_swp_project.dto.OrderDto;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface IOrderService {
    OrderDto createOrder(OrderDto orderDto, HttpServletRequest request);
    List<OrderDto> getOrdersByUserId();
    String processVNPayReturn(HttpServletRequest request) throws UnsupportedEncodingException;
    OrderDto getOrderDetail(Long orderId);
    String returnOrder(Long orderId);
    List<OrderDto> getOrdersByCurrentSupplier();
    String sendReminderEmail(Long orderId);
}
