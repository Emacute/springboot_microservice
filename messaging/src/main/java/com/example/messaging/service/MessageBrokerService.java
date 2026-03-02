package com.example.messaging.service;

import com.example.messaging.config.QueueProperties;
import com.example.messaging.config.RabbitMQConfig;
import com.example.messaging.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageBrokerService {

    private final RabbitTemplate rabbitTemplate;
    private final QueueProperties queueProperties;

    public void sendEmailMessage(String email, String name) {

        String messageContent = "Hello, " + name + " welcome to our system";

        EmailMessage emailMessage =
                new EmailMessage(email, messageContent);

        rabbitTemplate.convertAndSend(
                queueProperties.getEmailQueue(),
                emailMessage
        );
    }
}
