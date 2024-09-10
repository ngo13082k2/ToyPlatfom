package com.example.toyplatform_swp_project.controller;

import com.example.toyplatform_swp_project.dto.ToyDto;
import com.example.toyplatform_swp_project.exception.DataNotFoundException;
import com.example.toyplatform_swp_project.services.IToyservice;
import com.example.toyplatform_swp_project.services.implement.ToyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
            @RequestPart("imageFile") MultipartFile imageFile) throws DataNotFoundException, IOException {

        ToyDto toyDto;
        try {
            toyDto = objectMapper.readValue(toyJson, ToyDto.class);
            System.out.println("Received Toy Data: " + toyDto.toString());
        } catch (IOException e) {
            throw new RuntimeException("Invalid JSON format", e);
        }

        System.out.println("Received Image: " + imageFile.getOriginalFilename());

        ToyDto createdToy = toyService.createToy(toyDto, imageFile);
        return new ResponseEntity<>(createdToy, HttpStatus.CREATED);
    }
    @GetMapping("")
    public ResponseEntity<List<ToyDto>> getAllToys() {
        List<ToyDto> toys = toyService.getAllToys();
        return ResponseEntity.ok(toys);
    }
}