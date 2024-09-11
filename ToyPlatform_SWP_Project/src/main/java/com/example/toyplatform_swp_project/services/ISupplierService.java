package com.example.toyplatform_swp_project.services;

import com.example.toyplatform_swp_project.dto.SupplierDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ISupplierService {
    SupplierDto createSupplier(SupplierDto supplierDto, MultipartFile imageShopFile, MultipartFile backgroundImageFile) throws IOException;
}
