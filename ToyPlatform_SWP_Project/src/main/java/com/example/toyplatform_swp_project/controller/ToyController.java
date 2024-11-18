package com.example.toyplatform_swp_project.controller;

import com.example.toyplatform_swp_project.dto.ToyDto;
import com.example.toyplatform_swp_project.exception.DataNotFoundException;
import com.example.toyplatform_swp_project.services.IToyservice;
import com.example.toyplatform_swp_project.services.implement.ToyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/toys")
public class ToyController {
    private final IToyservice toyService;
    private final ObjectMapper objectMapper;
    @Autowired
    public ToyController(ToyService toyService, ObjectMapper objectMapper) {
        this.toyService = toyService;
        this.objectMapper = objectMapper;
    }
    @PostMapping("")
    public ResponseEntity<ToyDto> createToy(
            @RequestPart("toy") String toyJson,
            @RequestPart("imageFile") MultipartFile imageFile,
            HttpSession session) throws DataNotFoundException, IOException {

        ToyDto toyDto;
        try {
            toyDto = objectMapper.readValue(toyJson, ToyDto.class);
            System.out.println("Received Toy Data: " + toyDto.toString());
        } catch (IOException e) {
            throw new RuntimeException("Invalid JSON format", e);
        }

        System.out.println("Received Image: " + imageFile.getOriginalFilename());

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        toyDto.setSupplierId(userId);

        ToyDto createdToy = toyService.createToy(toyDto, imageFile);
        return new ResponseEntity<>(createdToy, HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<List<ToyDto>> getAllToys() {
        List<ToyDto> toys = toyService.getAllToys();
        return ResponseEntity.ok(toys);
    }
    @PutMapping("/{toyId}")
    public ResponseEntity<ToyDto> updateToy(
            @PathVariable Long toyId,
            @RequestPart("toy") String toyJson,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            HttpSession session) throws DataNotFoundException, IOException {

        ToyDto toyDto;
        try {
            toyDto = objectMapper.readValue(toyJson, ToyDto.class);
            System.out.println("Received Toy Data: " + toyDto.toString());
        } catch (IOException e) {
            throw new RuntimeException("Invalid JSON format", e);
        }

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        toyDto.setSupplierId(userId);

        ToyDto updatedToy = toyService.updateToy(toyId, toyDto, imageFile);

        return new ResponseEntity<>(updatedToy, HttpStatus.OK);
    }

    @GetMapping("/{toyId}")
    public ResponseEntity<ToyDto> getToyById(@PathVariable Long toyId) throws DataNotFoundException {
        ToyDto toyDto = toyService.getToyById(toyId);
        return new ResponseEntity<>(toyDto, HttpStatus.OK);
    }
    @GetMapping("/GetByCategory/{categoryId}")
    public ResponseEntity<List<ToyDto>> getToysByCategory(@PathVariable Long categoryId) throws DataNotFoundException {
        List<ToyDto> toyDtos = toyService.getToysByCategory(categoryId);
        return new ResponseEntity<>(toyDtos, HttpStatus.OK);
    }
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<ToyDto>> getToysBySupplierId(@PathVariable Long supplierId) {
        try {
            List<ToyDto> toys = toyService.getToysBySupplierId(supplierId);
            return ResponseEntity.ok(toys);
        } catch (DataNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/toys/user/{userId}")
    public ResponseEntity<List<ToyDto>> getToysByUserId(@PathVariable Long userId) {
        try {
            Long supplierId = toyService.getSupplierIdByUserId(userId);

            List<ToyDto> toys = toyService.getToysBySupplierId(supplierId);
            return ResponseEntity.ok(toys);
        } catch (DataNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/total")
    public ResponseEntity<Map<String,Long>> getTotalToys() {
        Map<String,Long> totalToys = toyService.getTotalToys();
        return ResponseEntity.ok(totalToys);
    }


}
