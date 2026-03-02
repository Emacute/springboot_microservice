package com.example.messaging.service;

import com.example.messaging.dto.PaymentDTO;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerConsumer {

    private final RestTemplate restTemplate;

    @RabbitListener(
            queues = "external.payment.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consumePayment(PaymentDTO paymentDTO) {

        log.info("Received payment from RabbitMQ: {}", paymentDTO);
        //        Merchant merchant = merchantRepository
//                .findByPayerName(paymentDTO.getPayerName())
//                .orElseThrow(() ->
//                        new RuntimeException("No merchant found for payerName"));
        // Default callback URL for now
        String callbackUrl = "http://localhost:8081/callback";
        // Let global exception handler handle any exception
        ResponseEntity<String> response = restTemplate.postForEntity(
                callbackUrl,
                paymentDTO,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(
                    "Callback failed with status: " + response.getStatusCode()
            );
        }

        log.info("✅ Callback sent successfully for paymentId={} to {}",
                paymentDTO.getPaymentId(), callbackUrl);
    }

}
