package com.fourformance.tts_vc_web.common.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // ConfirmCallback 설정: 메시지가 Exchange로 전송되었는지 확인.
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("Message was successfully sent to the exchange.");
            } else {
                System.err.println("Message failed to send to the exchange. Cause: " + cause);
            }
        });

        // ReturnsCallback 설정: 메시지가 큐로 전달되지 못했을 때 호출.
        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            System.err.println("Message was returned: " + returnedMessage);
        });

        return rabbitTemplate;
    }
}
