package com.example.toyplatform_swp_project.repository;

import com.example.toyplatform_swp_project.model.User;
import com.example.toyplatform_swp_project.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

}
