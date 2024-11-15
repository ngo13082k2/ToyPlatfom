package com.example.toyplatform_swp_project.controller;

import com.example.toyplatform_swp_project.dto.OrderDto;
import com.example.toyplatform_swp_project.model.Order;
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
    @PutMapping("/{orderId}/return")
    public ResponseEntity<String> returnOrder(@PathVariable Long orderId) {
        try {
            String message = orderService.returnOrder(orderId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/supplier/orders")
    public ResponseEntity<List<OrderDto>> getOrdersByCurrentSupplier() {
        try {
            List<OrderDto> orders = orderService.getOrdersByCurrentSupplier();
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @PostMapping("/{orderId}/send-reminder")
    public ResponseEntity<String> sendReminderEmail(@PathVariable Long orderId) {
        try {
            String result = orderService.sendReminderEmail(orderId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId, @RequestBody String note) {
        String result = orderService.cancelOrder(orderId, note);
        if ("Order has been canceled successfully.".equals(result)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }
    @GetMapping("/completed")
    public ResponseEntity<List<OrderDto>> getCompletedOrders() {
        List<OrderDto> completedOrders = orderService.getCompletedOrdersByCurrentSupplier();
        return ResponseEntity.ok(completedOrders);
    }
    @GetMapping("/rent")
    public ResponseEntity<List<OrderDto>> getRentOrders() {
        List<OrderDto> completedOrders = orderService.getRentOrdersByCurrentSupplier();
        return ResponseEntity.ok(completedOrders);
    }

    @PostMapping("/{orderId}/mark-shipped")
    public ResponseEntity<String> markOrderAsShipped(@PathVariable Long orderId) {
        orderService.updateOrderStatusToShipped(orderId);
        return ResponseEntity.ok("Order marked as shipped.");
    }
    @GetMapping("/canceled")
    public ResponseEntity<List<OrderDto>> getCanceledOrders() {
        List<OrderDto> canceledOrders = orderService.getCanceledOrdersByCurrentSupplier();
        return ResponseEntity.ok(canceledOrders);
    }

}