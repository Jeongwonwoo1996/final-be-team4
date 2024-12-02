package com.fourformance.tts_vc_web.common.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
// 개발 환경에서만 사용
public class RabbitMQCleanConfig {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @PostConstruct
    public void deleteQueues() {
        // 큐 삭제
//        amqpAdmin.deleteQueue(TaskConfig.TTS_QUEUE);
//        amqpAdmin.deleteQueue(TaskConfig.VC_QUEUE);
//        amqpAdmin.deleteQueue(TaskConfig.CONCAT_QUEUE);
//        amqpAdmin.deleteQueue(TaskConfig.DEAD_LETTER_QUEUE);

        System.out.println("All RabbitMQ queues have been deleted!");
    }
}
