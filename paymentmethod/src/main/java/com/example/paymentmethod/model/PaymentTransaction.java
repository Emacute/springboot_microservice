package com.example.paymentmethod.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @Column(name = "transaction_id")
    private String transactionId;
    private String paymentId;
    private String payerName;
    private Double amount;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
}
