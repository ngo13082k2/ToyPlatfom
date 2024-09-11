package com.example.toyplatform_swp_project.services.implement;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.toyplatform_swp_project.dto.SupplierDto;
import com.example.toyplatform_swp_project.model.Supplier;
import com.example.toyplatform_swp_project.model.Toy;
import com.example.toyplatform_swp_project.repository.SupplierRepository;
import com.example.toyplatform_swp_project.repository.ToyRepository;
import com.example.toyplatform_swp_project.services.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service

    public class SupplierService implements ISupplierService {
        @Autowired
        private final Cloudinary cloudinary;


        private final SupplierRepository supplierRepository;

        private final ToyRepository toyRepository;

        public SupplierService(Cloudinary cloudinary, SupplierRepository supplierRepository, ToyRepository toyRepository) {
            this.cloudinary = cloudinary;
            this.supplierRepository = supplierRepository;
            this.toyRepository = toyRepository;
        }
        public SupplierDto createSupplier(SupplierDto supplierDto, MultipartFile imageShopFile, MultipartFile backgroundImageFile) throws IOException {
            try {
                // Tải lên hình ảnh lên Cloudinary
                Map<String, Object> imageShopUploadResult = cloudinary.uploader().upload(imageShopFile.getBytes(), ObjectUtils.emptyMap());
                String imageShopUrl = imageShopUploadResult.get("url").toString();

                Map<String, Object> backgroundImageUploadResult = cloudinary.uploader().upload(backgroundImageFile.getBytes(), ObjectUtils.emptyMap());
                String backgroundImageUrl = backgroundImageUploadResult.get("url").toString();

                // Cập nhật URL hình ảnh vào DTO
                supplierDto.setImageShop(imageShopUrl);
                supplierDto.setBackgroundImage(backgroundImageUrl);

                // Tạo và lưu Supplier
                Supplier supplier = mapToEntity(supplierDto);
                Supplier savedSupplier = supplierRepository.save(supplier);
                return mapToDto(savedSupplier);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload images", e);
            }
        }

        public SupplierDto mapToDto(Supplier supplier) {
            SupplierDto dto = new SupplierDto();
            dto.setSupplierId(supplier.getSupplierId());
            dto.setImageShop(supplier.getImageShop());
            dto.setName(supplier.getName());
            dto.setDescription(supplier.getDescription());
            dto.setBackgroundImage(supplier.getBackgroundImage());
            dto.setUserId(supplier.getUser() != null ? supplier.getUser().getUserId() : null);

//            Set<Long> toyIds = new HashSet<>();
//            for (Toy toy : supplier.getToys()) {
//                toyIds.add(toy.getToyId());
//            }
//            dto.setToyIds(toyIds);

            return dto;
        }

        public Supplier mapToEntity(SupplierDto dto) {
            Supplier.SupplierBuilder builder = Supplier.builder()
                    .supplierId(dto.getSupplierId())
                    .imageShop(dto.getImageShop())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .backgroundImage(dto.getBackgroundImage());





//            Set<Toy> toys = new HashSet<>();
//            for (Long toyId : dto.getToyIds()) {
//                Toy toy = toyRepository.findById(toyId).orElse(null);
//                if (toy != null) {
//                    toys.add(toy);
//                }
//            }
//            builder.toys(toys);

            return builder.build();
        }


    }

