package com.example.toyplatform_swp_project.services.implement;

import com.example.toyplatform_swp_project.config.VNPayConfig;
import com.example.toyplatform_swp_project.dto.OrderDto;
import com.example.toyplatform_swp_project.dto.RentalDto;
import com.example.toyplatform_swp_project.model.*;
import com.example.toyplatform_swp_project.repository.*;
import com.example.toyplatform_swp_project.services.IOrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private JavaMailSender mailSender;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private ToyRepository toyRepository;
    @Autowired
    private HttpSession session;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private SupplierRepository supplierRepository;

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
    User user = order.getUser();
    if (user != null) {
        orderDto.setUserName(user.getFullName());

    }
    String phoneNumber = orderDto.getPhoneNumber();
    String address = orderDto.getAddress();

    if (phoneNumber != null) {
        order.setPhoneNumber(phoneNumber);
    }

    if (address != null) {
        order.setAddress(address);
    }

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
        if (rental != null) {
            order.setRequestDate(rental.getRequestDate());
            order.setDueDate(rental.getDueDate());
        }
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
    public OrderDto getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        return mapToDtoGetRental(order);
    }

    private OrderDto mapToDtoGetRental(Order order) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser().getUserId());
        dto.setUsername(order.getUser().getEmail());
        dto.setRentalId(order.getRental().getRentalId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setOrderType(order.getOrderType());
        if (order.getVoucher() != null) {
            dto.setVoucherCode(order.getVoucher().getCode());
        }
        dto.setStatus(order.getStatus());
        dto.setNote(order.getNote());

        if (order.getRental() != null) {
            Rental rental = order.getRental();
            RentalDto rentalDto = new RentalDto();
            rentalDto.setRentalId(rental.getRentalId());
            rentalDto.setRequestDate(rental.getRequestDate());
            rentalDto.setRentalPrice(rental.getRentalPrice());
            rentalDto.setTotalPrice(rental.getTotalPrice());
            rentalDto.setRentalDuration(rental.getRentalDuration());
            rentalDto.setQuantity(rental.getQuantity());

            if (rental.getToy() != null) {
                rentalDto.setToyName(rental.getToy().getName()); // Lấy tên từ Toy
            }

            dto.setRental(rentalDto);
        }

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

                    Rental rental = order.getRental();
                    if (rental != null) {
                        Toy toy = rental.getToy();
                        int rentalQuantity = rental.getQuantity();
                        if (toy != null && toy.getAmount() >= rentalQuantity) {
                            toy.setAmount(toy.getAmount() - rentalQuantity);
                            toyRepository.save(toy);
                        } else {
                            return "Số lượng đồ chơi không đủ để hoàn tất giao dịch!";
                        }
                    } else {
                        return "Không tìm thấy thông tin thuê đồ chơi phù hợp!";
                    }
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
    public String returnOrder(Long orderId) {
        User currentUser = authenticationService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No user is currently logged in.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        if (!order.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("You are not authorized to return this order.");
        }

        if (!"completed".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Only completed orders can be returned.");
        }

        order.setStatus("returned");
        orderRepository.save(order);

        return "Order has been successfully returned.";
    }
    public List<OrderDto> getOrdersByCurrentSupplier() {
        User currentUser = authenticationService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No user is currently logged in.");
        }

        Supplier supplier = supplierRepository.findByUser_UserId(currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("No supplier found for the current user."));

        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> order.getRental() != null &&
                        order.getRental().getToy() != null &&
                        order.getRental().getToy().getSuppliers().contains(supplier))
                .collect(Collectors.toList());

        return orders.stream().map(this::mapToDtoGetRental).collect(Collectors.toList());
    }
    public String sendReminderEmail(Long orderId) {
        User currentUser = authenticationService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No user is currently logged in.");
        }

        // Tìm đơn hàng và kiểm tra quyền truy cập
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Kiểm tra xem người dùng hiện tại có phải là nhà cung cấp sản phẩm không
        if (!order.getRental().getToy().getSuppliers().stream()
                .anyMatch(supplier -> supplier.getUser().getUserId().equals(currentUser.getUserId()))) {
            throw new RuntimeException("You are not authorized to send emails for this order.");
        }

        String userEmail = order.getUser().getEmail(); // Email của người dùng thuê
        sendEmail(userEmail, "Reminder: Your rental is about to expire, ",
                "Dear " + userEmail + ",\n\n"
                        + "We hope you're enjoying your rental of \"" + order.getRental().getToy().getName() + "\".\n"
                        + "This is a friendly reminder that your rental period is nearing its end. Please ensure that you return the item by the due date: "
                        + order.getRental().getDueDate() + ".\n\n"
                        + "If you have any questions or need assistance, feel free to contact us.\n\n"
                        + "Thank you for choosing our service!\n"
                        + "Best regards,\n"
                        + "The Toy Company Team\n");

        return "Reminder email sent to " + userEmail;
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public List<OrderDto> getCompletedOrdersByCurrentSupplier() {
        // Lấy người dùng hiện tại từ phiên đăng nhập
        User currentUser = authenticationService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No user is currently logged in.");
        }

        // Tìm supplier dựa trên người dùng hiện tại
        Supplier supplier = supplierRepository.findByUser_UserId(currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("No supplier found for the current user."));

        // Lấy tất cả đơn hàng có trạng thái "completed" và lọc theo supplier
        List<Order> orders = orderRepository.findByStatus("completed").stream()
                .filter(order -> order.getRental() != null &&
                        order.getRental().getToy() != null &&
                        order.getRental().getToy().getSuppliers().contains(supplier))
                .collect(Collectors.toList());

        // Chuyển đổi danh sách đơn hàng thành danh sách DTO
        return orders.stream().map(this::mapToDtoGetRental).collect(Collectors.toList());
    }
    public void updateOrderStatusToShipped(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus("sent");
            orderRepository.save(order);

            User user = order.getUser();
            if (user != null && user.getEmail() != null) {
                sendEmail(user.getEmail(), "Your Order Status Update",
                        "Dear " + user.getEmail() + ",\n\nYour order with ID " + orderId +
                                " has been marked as 'sent'. Thank you for using our service.\n\nBest regards,\nToy Platform Team");
            }
        }
    }
    public String cancelOrder(Long orderId, String note) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if ("completed".equalsIgnoreCase(order.getStatus())) {
                order.setStatus("canceled");
                order.setNote(note);
                orderRepository.save(order);
                return "Order has been canceled successfully.";
            } else {
                return "Only completed orders can be canceled.";
            }
        } else {
            return "Order not found.";
        }
    }
    public List<OrderDto> getCanceledOrdersByCurrentSupplier() {
        User currentUser = authenticationService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No user is currently logged in.");
        }

        Supplier supplier = supplierRepository.findByUser_UserId(currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("No supplier found for the current user."));

        // Lấy tất cả đơn hàng có trạng thái "canceled" và lọc theo supplier
        List<Order> orders = orderRepository.findByStatus("canceled").stream()
                .filter(order -> order.getRental() != null &&
                        order.getRental().getToy() != null &&
                        order.getRental().getToy().getSuppliers().contains(supplier))
                .collect(Collectors.toList());

        // Chuyển đổi danh sách đơn hàng thành danh sách DTO
        return orders.stream().map(this::mapToDtoGetRental).collect(Collectors.toList());
    }





}

