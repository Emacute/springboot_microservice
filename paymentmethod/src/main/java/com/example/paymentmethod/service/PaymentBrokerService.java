package com.example.paymentmethod.service;

import com.example.paymentmethod.model.PaymentTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentBrokerService {

    private final RabbitTemplate rabbitTemplate;

    public void sendPayment(PaymentTransaction paymentTransaction) {
        // send to the exchange or queue defined by consumer
        rabbitTemplate.convertAndSend(
                "external.payment.queue",
                paymentTransaction
        );
    }
}
