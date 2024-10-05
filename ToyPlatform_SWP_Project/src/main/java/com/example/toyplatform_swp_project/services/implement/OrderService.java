package com.example.toyplatform_swp_project.services.implement;

import com.example.toyplatform_swp_project.dto.OrderDto;
import com.example.toyplatform_swp_project.model.Order;
import com.example.toyplatform_swp_project.model.Rental;
import com.example.toyplatform_swp_project.model.User;
import com.example.toyplatform_swp_project.model.Voucher;
import com.example.toyplatform_swp_project.repository.OrderRepository;
import com.example.toyplatform_swp_project.repository.RentalRepository;
import com.example.toyplatform_swp_project.repository.UserRepository;
import com.example.toyplatform_swp_project.repository.VoucherRepository;
import com.example.toyplatform_swp_project.services.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private AuthenticationService authenticationService;
    public OrderDto createOrder(OrderDto orderDto) {
        Order order = mapToEntity(orderDto);

        if (order.getRental() != null) {
            Double rentalPrice = order.getRental().getRentalPrice();
            if (order.getVoucher() != null) {
                Double discount = order.getVoucher().getDiscount();
                if (discount != null && rentalPrice != null) {
                    Double totalPrice = rentalPrice * (1-discount) ;
                    order.setTotalPrice(totalPrice);
                } else {
                    order.setTotalPrice(rentalPrice);
                }
            } else {
                order.setTotalPrice(rentalPrice);
            }
        }

        Order savedOrder = orderRepository.save(order);
        return mapToDto(savedOrder);
    }
    public List<OrderDto> getOrdersByUserId() {
        User currentUser = authenticationService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No user is currently logged in.");
        }

        List<Order> orders = orderRepository.findByUserUserId(currentUser.getUserId());

        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private Order mapToEntity(OrderDto dto) {
        Order order = new Order();
        order.setOrderId(dto.getOrderId());

        User currentUser = authenticationService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No user is currently logged in.");
        }

        order.setUser(currentUser);

        Rental rental = rentalRepository.findById(dto.getRentalId())
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + dto.getRentalId()));
        order.setRental(rental);

        order.setOrderDate(dto.getOrderDate());
        order.setOrderType(dto.getOrderType());

        if (dto.getVoucherId() != null) {
            Voucher voucher = voucherRepository.findById(dto.getVoucherId())
                    .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + dto.getVoucherId()));
            order.setVoucher(voucher);
        }

        order.setStatus(dto.getStatus());

        return order;
    }

    private OrderDto mapToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser().getUserId());
        dto.setRentalId(order.getRental().getRentalId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setOrderType(order.getOrderType());
        if (order.getVoucher() != null) {
            dto.setVoucherId(order.getVoucher().getVoucherId());
        }
        dto.setStatus(order.getStatus());

        return dto;
    }
}

