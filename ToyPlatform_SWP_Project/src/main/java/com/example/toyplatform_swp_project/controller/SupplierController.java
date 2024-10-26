package com.example.toyplatform_swp_project.controller;

import com.example.toyplatform_swp_project.dto.SupplierDto;
import com.example.toyplatform_swp_project.exception.DataNotFoundException;
import com.example.toyplatform_swp_project.model.Order;
import com.example.toyplatform_swp_project.model.Supplier;
import com.example.toyplatform_swp_project.response.OrderResponseDto;
import com.example.toyplatform_swp_project.services.ISupplierService;
import com.example.toyplatform_swp_project.services.implement.SupplierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    @Autowired
    private ISupplierService supplierService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<SupplierDto> createSupplier(
            @RequestPart("supplierJson") String supplierJson,
            @RequestPart("imageShop") MultipartFile imageShopFile,
            @RequestPart("backgroundImage") MultipartFile backgroundImageFile
    ) {
        try {
            SupplierDto supplierDto = objectMapper.readValue(supplierJson, SupplierDto.class);

            SupplierDto createdSupplier = supplierService.createSupplier(supplierDto, imageShopFile, backgroundImageFile);
            return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/{supplierId}/orders")
    public List<OrderResponseDto> getCompletedOrdersBySupplierId(@PathVariable Long supplierId) {
        return supplierService.getCompletedOrdersBySupplierId(supplierId);
    }
    @GetMapping("/{supplierId}/total-revenue")
    public Map<String, Object> getTotalRentalRevenueBySupplierId(@PathVariable Long supplierId) {
        Double totalRevenue = supplierService.calculateTotalRentalRevenueBySupplierId(supplierId);

        Map<String, Object> response = new HashMap<>();
        response.put("totalRentalRevenue", totalRevenue);

        return response;
    }
    @GetMapping("/{supplierId}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long supplierId) {
        try {
            Supplier supplier = supplierService.getSupplierById(supplierId);
            return ResponseEntity.ok(supplier);
        } catch (DataNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
