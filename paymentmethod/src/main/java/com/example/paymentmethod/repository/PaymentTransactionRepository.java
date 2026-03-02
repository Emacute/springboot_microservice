package com.example.paymentmethod.repository;

import com.example.paymentmethod.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository
        extends JpaRepository<PaymentTransaction, String> {
}
