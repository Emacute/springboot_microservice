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
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitMQConfig {
    private final QueueProperties queueProperties;


    public RabbitMQConfig(QueueProperties queueProperties) {
        this.queueProperties = queueProperties;
    }

    // =========================
    // EMAIL QUEUE
    // =========================
    @Bean
    public Queue emailQueue() {
        return new Queue(queueProperties.getEmailQueue(), true);
    }

    // =========================
    // PAYMENT DEAD LETTER QUEUE
    // =========================
    @Bean
    public Queue paymentDeadLetterQueue() {
        return new Queue(queueProperties.getPaymentDlq(), true);
    }

    // =========================
    // EXTERNAL PAYMENT QUEUE (with DLQ)
    // =========================
    @Bean
    public Queue externalPaymentQueue() {
        return QueueBuilder.durable(queueProperties.getExternalPaymentQueue())
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", queueProperties.getPaymentDlq())
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
                        .maxAttempts(5)
                        .backOffOptions(
                                60000,
                                1.0,
                                60000
                        )
                        .build()
        );

        return factory;
    }
}
