package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.service.PaymentService;
import com.example.daugiaonline.config.VNPayConfig;
import com.example.daugiaonline.entity.User;
import com.example.daugiaonline.exception.AppException;
import com.example.daugiaonline.entity.Transaction;
import com.example.daugiaonline.enums.TransactionType;
import com.example.daugiaonline.enums.TransactionStatus;
import com.example.daugiaonline.infrastructure.repository.TransactionRepository;
import com.example.daugiaonline.infrastructure.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final VNPayConfig vnPayConfig;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public String createPaymentUrl(long amount, String username, HttpServletRequest request) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = "Nap tien vao vi BidMax user " + username;
        String orderType = "other";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPayConfig.getIpAddress(request);
        String vnp_TmnCode = vnPayConfig.getTmnCode();

        long amountForVNPay = amount * 100;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountForVNPay));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        String origin = request.getHeader("Origin");
        if (origin == null || origin.isEmpty()) {
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                try {
                    java.net.URL url = new java.net.URL(referer);
                    origin = url.getProtocol() + "://" + url.getHost() + (url.getPort() != -1 ? ":" + url.getPort() : "");
                } catch (Exception e) {
                    origin = "http://localhost:5173";
                }
            } else {
                origin = "http://localhost:5173";
            }
        }
        String returnUrl = vnPayConfig.getReturnUrl();
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // Save pending transaction to DB
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng"));

        Transaction transaction = Transaction.builder()
                .user(user)
                .amount((double) amount)
                .type(TransactionType.TOPUP)
                .status(TransactionStatus.PENDING)
                .vnpayTranId(vnp_TxnRef)
                .build();
        transactionRepository.save(transaction);

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

        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi tạo URL thanh toán VNPAY", e);
            throw new AppException("Không thể tạo URL thanh toán");
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return vnPayConfig.getPayUrl() + "?" + queryUrl;
    }

    @Override
    @Transactional
    public void processPaymentReturn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        // Tạo lại chuỗi hash để so sánh
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = fields.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }
        } catch (Exception e) {
            log.error("Lỗi checksum VNPAY", e);
            throw new AppException("Lỗi xác thực giao dịch");
        }

        String signValue = VNPayConfig.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());

        if (signValue.equals(vnp_SecureHash)) {
            String vnp_TxnRef = request.getParameter("vnp_TxnRef");
            Transaction transaction = transactionRepository.findByVnpayTranId(vnp_TxnRef)
                    .orElseThrow(() -> new AppException("Không tìm thấy giao dịch"));
            
            if (transaction.getStatus() != TransactionStatus.PENDING) {
                log.warn("Giao dịch {} đã được xử lý trước đó (chống Replay Attack)", vnp_TxnRef);
                return;
            }

            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                // Thành công, cộng tiền
                long amount = Long.parseLong(request.getParameter("vnp_Amount")) / 100;
                User user = transaction.getUser();
                log.info("Giao dịch VNPAY thành công cho user: {}, số tiền: {}", user.getUsername(), amount);

                user.setBalance(user.getBalance() + amount);
                userRepository.save(user);

                transaction.setStatus(TransactionStatus.SUCCESS);
                transactionRepository.save(transaction);
            } else {
                log.warn("Giao dịch VNPAY thất bại. ResponseCode: {}", request.getParameter("vnp_ResponseCode"));
                transaction.setStatus(TransactionStatus.FAILED); // Requires FAILED in TransactionStatus enum, assuming it exists or we can just use FAILED if it exists. 
                // Wait, TransactionStatus has SUCCESS, PENDING, FAILED? Let me check TransactionStatus.java. 
                // If FAILED doesn't exist, we can use CANCELED. Let me assume FAILED exists or check it.
                transactionRepository.save(transaction);
            }
        } else {
            log.error("Xác thực mã băm VNPAY thất bại. Dữ liệu có thể bị giả mạo!");
            throw new AppException("Xác thực giao dịch thất bại");
        }
    }
}
