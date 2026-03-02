package com.example.paymentmethod.service;

import com.example.paymentmethod.dto.PaymentDTO;
import com.example.paymentmethod.model.PaymentTransaction;
import com.example.paymentmethod.repository.PaymentTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final PaymentBrokerService paymentBrokerService;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Transactional
    public ResponseEntity<?> createPayment(PaymentDTO paymentDTO) {

        log.info("Starting payment processing for paymentId={} by payer={}",
                paymentDTO.getPaymentId(), paymentDTO.getPayerName());

        // Validate email format
        if (!EMAIL_PATTERN.matcher(paymentDTO.getPayerName()).matches()) {
            log.warn("Invalid payer name/email format: {}", paymentDTO.getPayerName());
            throw new RuntimeException("Invalid payer name/email format: " + paymentDTO.getPayerName());
        }
        // Process with 90% success probability
        boolean success = processPaymentWithProbability();
        if (!success) {
            log.warn("Payment processing failed for paymentId={}", paymentDTO.getPaymentId());
            throw new RuntimeException("Payment failed due to processing error.");
        }
        // Generate transaction ID
        String transactionId = "TXN-" + UUID.randomUUID();
        log.info("Payment successful, generated transactionId={}", transactionId);

        // Create entity
        PaymentTransaction transaction = PaymentTransaction.builder()
                .transactionId(transactionId)
                .paymentId(paymentDTO.getPaymentId())
                .payerName(paymentDTO.getPayerName())
                .amount(paymentDTO.getAmount())
                .currency(paymentDTO.getCurrency())
                .status("SUCCESS")
                .createdAt(LocalDateTime.now())
                .build();

        // Save to database
        PaymentTransaction savedTransaction = paymentTransactionRepository.save(transaction);
        log.info("Payment transaction saved with transactionId={}", savedTransaction.getTransactionId());

        // Send to RabbitMQ after saving
        paymentBrokerService.sendPayment(savedTransaction);
        log.info("Payment transaction sent to RabbitMQ for transactionId={}", savedTransaction.getTransactionId());

        // Return response (global exception handler will catch exceptions automatically)
        return ResponseEntity.status(201).body(savedTransaction); // HTTP 201 Created
    }

    // 90% chance success
    private boolean processPaymentWithProbability() {
        Random random = new Random();
        int chance = random.nextInt(100);
        return chance < 90; // 90% chance of success
    }
}
