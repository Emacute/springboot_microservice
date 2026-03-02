package com.example.messaging.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QueueProperties {

    @Value("${messaging.queue.email}")
    private String emailQueue;

    @Value("${messaging.queue.external-payment}")
    private String externalPaymentQueue;

    @Value("${messaging.queue.payment-dlq}")
    private String paymentDlq;

    public String getEmailQueue() {
        return emailQueue;
    }

    public String getExternalPaymentQueue() {
        return externalPaymentQueue;
    }

    public String getPaymentDlq() {
        return paymentDlq;
    }
}
