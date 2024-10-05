package com.example.toyplatform_swp_project.controller;

import com.example.toyplatform_swp_project.dto.OrderDto;
import com.example.toyplatform_swp_project.services.implement.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        OrderDto createdOrder = orderService.createOrder(orderDto);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId() {
        List<OrderDto> orders = orderService.getOrdersByUserId();
        return ResponseEntity.ok(orders);
    }
}