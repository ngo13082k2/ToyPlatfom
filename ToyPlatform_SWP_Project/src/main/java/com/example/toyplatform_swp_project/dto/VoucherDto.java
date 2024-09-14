package com.example.toyplatform_swp_project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class VoucherDto {
    private Long voucherId;
    private String code;
    private double discount;
    private LocalDateTime createAt;
    private LocalDateTime createEnd;
    private String status;
    private Long userId;
    private Long orderId;

}
