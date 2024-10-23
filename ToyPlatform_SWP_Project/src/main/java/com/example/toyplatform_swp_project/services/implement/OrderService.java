package com.example.toyplatform_swp_project.services.implement;

import com.example.toyplatform_swp_project.config.VNPayConfig;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {
    @Value("${vnpay.tmn_code}")
    private String vnp_TmnCode;

    @Value("${vnpay.hash_secret}")
    private String vnp_HashSecret;

    @Value("${vnpay.api_url}")
    private String vnp_PayUrl;

    @Value("${vnpay.return_url}")
    private String vnp_ReturnUrl;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private HttpSession session;
    @Autowired
    private AuthenticationService authenticationService;

//    public OrderDto createOrder(OrderDto orderDto) {
//        Long rentalId = (Long) session.getAttribute("currentRentalId");
//        if (rentalId == null) {
//            throw new RuntimeException("No rental is currently in session.");
//        }
//        Order order = mapToEntity(orderDto, rentalId);
//
//        if (order.getRental() != null) {
//            Double rentalPrice = order.getRental().getRentalPrice();
//            if (order.getVoucher() != null) {
//                Double discount = order.getVoucher().getDiscount();
//                if (discount != null && rentalPrice != null) {
//                    Double totalPrice = rentalPrice * (1-discount) ;
//                    order.setTotalPrice(totalPrice);
//                } else {
//                    order.setTotalPrice(rentalPrice);
//                }
//            } else {
//                order.setTotalPrice(rentalPrice);
//            }
//        }
//
//        Order savedOrder = orderRepository.save(order);
//        return mapToDto(savedOrder);
//    }
public OrderDto createOrder(OrderDto orderDto, HttpServletRequest request) {
    Long rentalId = (Long) session.getAttribute("currentRentalId");
    if (rentalId == null) {
        throw new RuntimeException("No rental is currently in session.");
    }

    Order order = mapToEntity(orderDto, rentalId);

    if (order.getRental() != null) {
        Double rentalPrice = order.getRental().getRentalPrice();
        if (order.getVoucher() != null) {
            Double discount = order.getVoucher().getDiscount();
            if (discount != null && rentalPrice != null) {
                Double totalPrice = rentalPrice * (1 - discount);
                order.setTotalPrice(totalPrice);
            } else {
                order.setTotalPrice(rentalPrice);
            }
        } else {
            order.setTotalPrice(rentalPrice);
        }
    }

    order.setStatus("pending");

    String vnpTxnRef = VNPayConfig.getRandomNumber(8);
    order.setTxnRef(vnpTxnRef);

    Order savedOrder = orderRepository.save(order);

    Double totalPrice = savedOrder.getTotalPrice();
    OrderDto result = mapToDto(savedOrder);
    if (totalPrice != null) {
        String paymentUrl = generatePaymentUrl(totalPrice.longValue(), vnpTxnRef, request);
        if (paymentUrl != null) {
            result.setPaymentUrl(paymentUrl);
        } else {
            System.out.println("Failed to generate payment URL.");
        }
    }
    return result;
}


    public List<OrderDto> getOrdersByUserId() {
        User currentUser = authenticationService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No user is currently logged in.");
        }

        List<Order> orders = orderRepository.findByUserUserId(currentUser.getUserId());

        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private Order mapToEntity(OrderDto dto,Long rentalId) {
        Order order = new Order();
        order.setOrderId(dto.getOrderId());

        User currentUser = authenticationService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No user is currently logged in.");
        }

        order.setUser(currentUser);

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + rentalId));
        order.setRental(rental);

        order.setOrderDate(dto.getOrderDate());
        order.setOrderType(dto.getOrderType());

        if (dto.getVoucherCode() != null) {
            Voucher voucher = voucherRepository.findByCode(dto.getVoucherCode())
                    .orElseThrow(() -> new RuntimeException("Voucher not found with code: " + dto.getVoucherCode()));
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
            dto.setVoucherCode(order.getVoucher().getCode());
        }
        dto.setStatus(order.getStatus());


        return dto;
    }
    public String generatePaymentUrl(Long amount, String vnpTxnRef, HttpServletRequest request) {
        try {
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_OrderInfo = "Thanh toán đơn hàng";
            String orderType = "billpayment";

            String vnp_IpAddr = VNPayConfig.getIpAddress(request);
            String locale = "vn";
            String currCode = "VND";

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
            vnp_Params.put("vnp_CurrCode", currCode);
            vnp_Params.put("vnp_TxnRef", vnpTxnRef); // Sử dụng mã giao dịch được truyền vào
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_OrderType", orderType);
            vnp_Params.put("vnp_Locale", locale);
            vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (String fieldName : fieldNames) {
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                            .append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append('&');
                    hashData.append('&');
                }
            }

            hashData.setLength(hashData.length() - 1);
            query.setLength(query.length() - 1);

            String secureHash = VNPayConfig.hmacSHA512(vnp_HashSecret, hashData.toString());
            query.append("&vnp_SecureHash=").append(secureHash);

            return vnp_PayUrl + "?" + query.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public String processVNPayReturn(HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String, String[]> paramMap = request.getParameterMap();
        List<String> fieldNames = new ArrayList<>(paramMap.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        String vnpSecureHash = "";

        // Loại bỏ vnp_SecureHash và vnp_SecureHashType khi tính toán chữ ký
        for (String fieldName : fieldNames) {
            if (!"vnp_SecureHash".equals(fieldName) && !"vnp_SecureHashType".equals(fieldName)) {
                String[] fieldValueArr = paramMap.get(fieldName);
                String fieldValue = fieldValueArr[0];
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                hashData.append('&');
            } else if ("vnp_SecureHash".equals(fieldName)) {
                vnpSecureHash = paramMap.get(fieldName)[0];
            }
        }

        if (hashData.length() > 0) {
            hashData.setLength(hashData.length() - 1);
        }

        String calculatedHash = VNPayConfig.hmacSHA512(vnp_HashSecret, hashData.toString());

        if (vnpSecureHash.equalsIgnoreCase(calculatedHash)) {
            String amount = request.getParameter("vnp_Amount");
            String responseCode = request.getParameter("vnp_ResponseCode");
            String vnpTxnRef = request.getParameter("vnp_TxnRef");

            if ("00".equals(responseCode)) {
                Order order = orderRepository.findByTxnRef(vnpTxnRef);
                if (order != null) {
                    order.setStatus("completed");
                    orderRepository.save(order);
                    return "Thanh toán thành công! Đơn hàng đã được hoàn tất.";
                } else {
                    return "Không tìm thấy đơn hàng phù hợp!";
                }
            } else {
                return "Giao dịch không thành công!";
            }
        } else {
            System.out.println("Chữ ký không hợp lệ! Dữ liệu tính toán: " + hashData.toString());
            System.out.println("Chữ ký tính toán: " + calculatedHash);
            System.out.println("Chữ ký VNPay trả về: " + vnpSecureHash);
            return "Chữ ký không hợp lệ!";
        }
    }


}

