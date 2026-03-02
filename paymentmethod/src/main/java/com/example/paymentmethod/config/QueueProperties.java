package com.example.paymentmethod.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QueueProperties {
    @Value("${payment.queue.external-payment}")
    private String externalPaymentQueue;
    public String getExternalPaymentQueue() {
        return externalPaymentQueue;
    }

}
