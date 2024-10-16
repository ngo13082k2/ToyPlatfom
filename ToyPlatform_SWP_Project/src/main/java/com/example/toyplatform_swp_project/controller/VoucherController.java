package com.example.toyplatform_swp_project.controller;

import com.example.toyplatform_swp_project.dto.VoucherDto;
import com.example.toyplatform_swp_project.exception.DataNotFoundException;
import com.example.toyplatform_swp_project.services.IVoucherService;
import com.example.toyplatform_swp_project.services.implement.VoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    private final IVoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }
    @PostMapping("")
    public ResponseEntity<VoucherDto> createVoucher(@RequestBody VoucherDto voucherDto) {
        try {
            VoucherDto createdVoucher = voucherService.createVoucher(voucherDto);
            return new ResponseEntity<>(createdVoucher, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
