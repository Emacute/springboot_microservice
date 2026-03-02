package com.example.messaging.repository;

import com.example.messaging.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository
        extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findByPayerName(String payerName);
}
