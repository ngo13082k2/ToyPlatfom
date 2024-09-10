package com.example.toyplatform_swp_project.services;

import com.example.toyplatform_swp_project.dto.ToyDto;
import com.example.toyplatform_swp_project.exception.DataNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IToyservice {
    ToyDto createToy(ToyDto toyDto, MultipartFile imageFile) throws IOException, DataNotFoundException;
    List<ToyDto> getAllToys();
}