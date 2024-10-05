package com.example.toyplatform_swp_project.services.implement;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.toyplatform_swp_project.dto.ToyDto;
import com.example.toyplatform_swp_project.exception.DataNotFoundException;
import com.example.toyplatform_swp_project.model.Category;
import com.example.toyplatform_swp_project.model.Toy;
import com.example.toyplatform_swp_project.repository.CategoryRepository;
import com.example.toyplatform_swp_project.repository.ToyRepository;
import com.example.toyplatform_swp_project.services.IToyservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ToyService implements IToyservice {
    @Autowired
    private final Cloudinary cloudinary;
    private final ToyRepository toyRepository;
    private final CategoryRepository categoryRepository;

    public ToyService(Cloudinary cloudinary, ToyRepository toyRepository, CategoryRepository categoryRepository) {
        this.cloudinary = cloudinary;
        this.toyRepository = toyRepository;
        this.categoryRepository = categoryRepository;
    }

    public ToyDto createToy(ToyDto toyDto, MultipartFile imageFile) throws IOException, DataNotFoundException {
        try {
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = uploadResult.get("url").toString();
            toyDto.setImage(imageUrl);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }

        Toy toy = mapToEntity(toyDto);
        Toy savedToy = toyRepository.save(toy);
        return mapToDto(savedToy);
    }

    public List<ToyDto> getAllToys() {
        List<Toy> toys = toyRepository.findAll();
        return toys.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    public ToyDto updateToy(Long toyId, ToyDto toyDto, MultipartFile imageFile) throws DataNotFoundException, IOException {
        Toy existingToy = toyRepository.findById(toyId)
                .orElseThrow(() -> new DataNotFoundException("Toy not found with id: " + toyId));

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = uploadResult.get("url").toString();
                toyDto.setImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        } else {
            toyDto.setImage(existingToy.getImage());
        }

        // Cập nhật các trường của toy
        existingToy.setName(toyDto.getName());
        existingToy.setDescription(toyDto.getDescription());
        existingToy.setPrice(toyDto.getPrice());
        existingToy.setAmount(toyDto.getAmount());
        existingToy.setSupplierId(toyDto.getSupplierId());  // SupplierId lấy từ session trong controller
        existingToy.setImage(toyDto.getImage());

        Category category = categoryRepository.findById(toyDto.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found with id: " + toyDto.getCategoryId()));
        existingToy.setCategory(category);

        Toy updatedToy = toyRepository.save(existingToy);

        return mapToDto(updatedToy);
    }

    public ToyDto getToyById(Long toyId) throws DataNotFoundException {
        Toy toy = toyRepository.findById(toyId)
                .orElseThrow(() -> new DataNotFoundException("Toy not found with id: " + toyId));

        return mapToDto(toy);
    }
    public List<ToyDto> getToysByCategory(Long categoryId) throws DataNotFoundException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DataNotFoundException("Category not found with id: " + categoryId));

        List<Toy> toys = toyRepository.findByCategory(category);
        if (toys.isEmpty()) {
            throw new DataNotFoundException("No toys found for category with id: " + categoryId);
        }
        return toys.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    private Toy mapToEntity(ToyDto toyDto) throws DataNotFoundException {
        Toy toy = Toy.builder()
                .name(toyDto.getName())
                .description(toyDto.getDescription())
                .price(toyDto.getPrice())
                .amount(toyDto.getAmount())
                .supplierId(toyDto.getSupplierId())
                .image(toyDto.getImage())
                .build();

        Category category = categoryRepository.findById(toyDto.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found with id: " + toyDto.getCategoryId()));
        toy.setCategory(category);


        return toy;
    }

    private ToyDto mapToDto(Toy toy) {
        ToyDto toyDto = new ToyDto();
        toyDto.setToyId(toy.getToyId());
        toyDto.setName(toy.getName());
        toyDto.setDescription(toy.getDescription());
        toyDto.setPrice(toy.getPrice());
        toyDto.setAmount(toy.getAmount());
        toyDto.setSupplierId(toy.getSupplierId());
        toyDto.setImage(toy.getImage());
        toyDto.setCategoryId(toy.getCategory().getCategoryId());

        return toyDto;
    }

}
