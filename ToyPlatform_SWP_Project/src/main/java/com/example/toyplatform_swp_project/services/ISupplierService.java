package com.example.toyplatform_swp_project.services;

import com.example.toyplatform_swp_project.dto.SupplierDto;
import com.example.toyplatform_swp_project.exception.DataNotFoundException;
import com.example.toyplatform_swp_project.model.Order;
import com.example.toyplatform_swp_project.model.Supplier;
import com.example.toyplatform_swp_project.response.OrderResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ISupplierService {
    SupplierDto createSupplier(SupplierDto supplierDto, MultipartFile imageShopFile, MultipartFile backgroundImageFile) throws IOException;
    List<OrderResponseDto> getCompletedOrdersBySupplierId(Long supplierId);
    Double calculateTotalRentalRevenueBySupplierId(Long supplierId);
    Supplier getSupplierById(Long supplierId) throws DataNotFoundException;
}
