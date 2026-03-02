package com.example.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RabbitMQConfig {

    // =========================
    // QUEUE NAMES
    // =========================
    public static final String EMAIL_QUEUE = "bulk.email.queue";
    public static final String EXTERNAL_PAYMENT_QUEUE = "external.payment.queue";
    public static final String PAYMENT_DLQ = "external.payment.dlq";

    // =========================
    // EMAIL QUEUE
    // =========================
    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    // =========================
    // PAYMENT DEAD LETTER QUEUE
    // =========================
    @Bean
    public Queue paymentDeadLetterQueue() {
        return new Queue(PAYMENT_DLQ, true);
    }

    // =========================
    // EXTERNAL PAYMENT QUEUE (with DLQ)
    // =========================
    @Bean
    public Queue externalPaymentQueue() {
        return QueueBuilder.durable(EXTERNAL_PAYMENT_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", PAYMENT_DLQ)
                .build();
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    // =========================
    // JSON MESSAGE CONVERTER
    // =========================
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // =========================
    // RABBIT TEMPLATE
    // =========================
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    // =========================
    // RETRY CONFIGURATION
    // =========================
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        factory.setAdviceChain(
                RetryInterceptorBuilder.stateless()
                        .maxAttempts(5)          // retry 5 times
                        .backOffOptions(
                                60000,           // 60 seconds initial delay
                                1.0,             // multiplier
                                60000            // max delay
                        )
                        .build()
        );

        return factory;
    }
}
