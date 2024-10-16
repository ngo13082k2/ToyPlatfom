package com.example.toyplatform_swp_project.services;

import com.example.toyplatform_swp_project.dto.VoucherDto;
import com.example.toyplatform_swp_project.exception.DataNotFoundException;

public interface IVoucherService {
    VoucherDto createVoucher(VoucherDto voucherDto) throws DataNotFoundException;
}
