package com.example.toyplatform_swp_project.response;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class OrderResponseDto {
    // Getters and Setters
    private Long orderId;
    private String username;
    private Long toyId; // Thêm toyId từ rental
    private Integer rentalDuration; // Thêm rentalDuration từ rental
    private LocalDate orderDate;
    private Double totalPrice;
    private String orderType;
    private String status;

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setToyId(Long toyId) {
        this.toyId = toyId;
    }

    public void setRentalDuration(Integer rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
