package com.example.toyplatform_swp_project.services;

import com.example.toyplatform_swp_project.dto.OrderDto;
import com.example.toyplatform_swp_project.model.Order;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface IOrderService {
    OrderDto createOrder(OrderDto orderDto, HttpServletRequest request);
    List<OrderDto> getOrdersByUserId();
    String processVNPayReturn(HttpServletRequest request) throws UnsupportedEncodingException;
    OrderDto getOrderDetail(Long orderId);
    String returnOrder(Long orderId);
    List<OrderDto> getOrdersByCurrentSupplier();
    String sendReminderEmail(Long orderId);
    List<OrderDto> getCompletedOrdersByCurrentSupplier();
    void updateOrderStatusToShipped(Long orderId);
    String cancelOrder(Long orderId, String note);
    List<OrderDto> getCanceledOrdersByCurrentSupplier();
    List<OrderDto> getRentOrdersByCurrentSupplier();
    List<OrderDto> getSentOrdersByCurrentSupplier();
    Map<String, Long> getTotalOrdersByStatuses();
}
