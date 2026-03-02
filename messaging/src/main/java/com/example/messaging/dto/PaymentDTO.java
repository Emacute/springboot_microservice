package com.example.messaging.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private String transactionId;
    private String paymentId;
    private String payerName;
    private Double amount;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
}
