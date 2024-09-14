package com.example.toyplatform_swp_project.controller;

import com.example.toyplatform_swp_project.dto.RentalDto;
import com.example.toyplatform_swp_project.services.implement.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @PostMapping
    public ResponseEntity<RentalDto> createRental(@RequestBody RentalDto rentalDto) {
        RentalDto createdRental = rentalService.createRental(rentalDto);
        return new ResponseEntity<>(createdRental, HttpStatus.CREATED);
    }

}
