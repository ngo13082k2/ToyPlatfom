package com.example.toyplatform_swp_project.controller;

import com.example.toyplatform_swp_project.dto.SupplierDto;
import com.example.toyplatform_swp_project.services.implement.SupplierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

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
}
