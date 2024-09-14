package com.example.toyplatform_swp_project.services.implement;

import com.example.toyplatform_swp_project.dto.VoucherDto;
import com.example.toyplatform_swp_project.exception.DataNotFoundException;
import com.example.toyplatform_swp_project.model.User;
import com.example.toyplatform_swp_project.model.Voucher;
import com.example.toyplatform_swp_project.repository.UserRepository;
import com.example.toyplatform_swp_project.repository.VoucherRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
//    private final OrderRepository orderRepository;

    public VoucherService(VoucherRepository voucherRepository, UserRepository userRepository) {
        this.voucherRepository = voucherRepository;
        this.userRepository = userRepository;

    }

    public VoucherDto createVoucher(VoucherDto voucherDto) throws DataNotFoundException {
        Voucher voucher = mapToEntity(voucherDto);
        String voucherCode = generateVoucherCode(voucherDto.getDiscount());
        voucher.setCode(voucherCode);
        voucher.setStatus("ACTIVE");
        Voucher savedVoucher = voucherRepository.save(voucher);

        return mapToDto(savedVoucher);
    }

    private Voucher mapToEntity(VoucherDto voucherDto) throws DataNotFoundException {
        Voucher voucher = Voucher.builder()
                .discount(voucherDto.getDiscount())
                .createAt(LocalDateTime.now())
                .createEnd(voucherDto.getCreateEnd())
                .build();

        User user = userRepository.findById(voucherDto.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + voucherDto.getUserId()));
        voucher.setUser(user);

//        if (voucherDto.getOrderId() != null) {
//            Order order = orderRepository.findById(voucherDto.getOrderId())
//                    .orElseThrow(() -> new DataNotFoundException("Order not found with id: " + voucherDto.getOrderId()));
//            voucher.setOrder(order);
//        }
        return voucher;
    }
    private VoucherDto mapToDto(Voucher voucher) {
        VoucherDto voucherDto = new VoucherDto();
        voucherDto.setVoucherId(voucher.getVoucherId());
        voucherDto.setCode(voucher.getCode());
        voucherDto.setDiscount(voucher.getDiscount());
        voucherDto.setCreateAt(voucher.getCreateAt());
        voucherDto.setCreateEnd(voucher.getCreateEnd());
        voucherDto.setStatus(voucher.getStatus());
        voucherDto.setUserId(voucher.getUser().getUserId());

        return voucherDto;
    }
    private String generateVoucherCode(double discount) {
        String randomPart = generateRandomString(5);
        return "DIS" + (int) discount + randomPart;
    }
    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }
}
