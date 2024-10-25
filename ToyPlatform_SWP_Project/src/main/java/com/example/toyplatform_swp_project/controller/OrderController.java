package com.example.toyplatform_swp_project.controller;

import com.example.toyplatform_swp_project.dto.OrderDto;
import com.example.toyplatform_swp_project.services.IOrderService;
import com.example.toyplatform_swp_project.services.implement.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private IOrderService orderService;

//    @PostMapping
//    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
//        OrderDto createdOrder = orderService.createOrder(orderDto);
//        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
//    }
    @PostMapping("/create")
    public OrderDto createOrder(@RequestBody OrderDto orderDto, HttpServletRequest request) {
        return orderService.createOrder(orderDto, request);
    }
    @GetMapping("/vnpay-return")
    public String processVNPayReturn(HttpServletRequest request) {
        try {
            return orderService.processVNPayReturn(request);
        } catch (Exception e) {
            return "Có lỗi xảy ra trong quá trình xử lý thanh toán: " + e.getMessage();
        }
    }


    @GetMapping("/user")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId() {
        List<OrderDto> orders = orderService.getOrdersByUserId();
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderDetail(@PathVariable Long orderId) {
        OrderDto orderDetail = orderService.getOrderDetail(orderId);
        return ResponseEntity.ok(orderDetail);
    }

}