package com.example.paymentmethod.controller;

import com.example.paymentmethod.dto.PaymentDTO;
import com.example.paymentmethod.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class paymentController {
    private final PaymentService paymentService;
    // submit payment
    @PostMapping("/create")
    public ResponseEntity<?> uploadExcel(@Valid @RequestBody PaymentDTO paymentDTO) {
       return paymentService.createPayment(paymentDTO);
    }
}
