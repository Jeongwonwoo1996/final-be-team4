package com.fourformance.tts_vc_web.common.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TaskConfig {

    public static final String EXCHANGE_NAME = "audioTaskExchange";
    public static final String TTS_QUEUE = "audioTTSQueue";
    public static final String VC_QUEUE = "audioVCQueue";
    public static final String CONCAT_QUEUE = "audioConcatQueue";

    public static final String DLX_NAME = "audioDLX"; // Dead Letter Exchange 이름
    public static final String DEAD_LETTER_QUEUE = "audioDLQ"; // Dead Letter Queue 이름
    // 실제 운영 환경에서는 모든 실패를 DLQ로 보내기 전에 로깅과 알림 시스템을 통해 원인을 파악하는 것이 중요

    // Main Exchange 생성
    @Bean
    public DirectExchange audioTaskExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // Dead Letter Exchange 생성
    @Bean
    public DirectExchange audioDLX() {
        return new DirectExchange(DLX_NAME);
    }

    // Task Queues 생성 (TTS, VC, CONCAT)
    @Bean(name = "ttsQueue")
    public Queue ttsQueue() {
        return QueueBuilder.durable(TTS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_NAME) // Dead Letter Exchange로 이동 설정
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_QUEUE) // Dead Letter Queue로 이동 설정
                .build();
    }

    @Bean(name = "vcQueue")
    public Queue vcQueue() {
        return QueueBuilder.durable(VC_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_QUEUE)
                .build();
    }

    @Bean(name = "concatQueue")
    public Queue concatQueue() {
        return QueueBuilder.durable(CONCAT_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_QUEUE)
                .build();
    }

    // Dead Letter Queue 생성
    @Bean(name = "dlq")
    public Queue dlq() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE)
                .build();
    }

    // Task Queues Binding 설정
    @Bean
    public Binding bindTTSQueue(@Qualifier("ttsQueue") Queue ttsQueue, DirectExchange audioTaskExchange) {
        return BindingBuilder.bind(ttsQueue).to(audioTaskExchange).with("tts");
    }

    @Bean
    public Binding bindVCQueue(@Qualifier("vcQueue") Queue vcQueue, DirectExchange audioTaskExchange) {
        return BindingBuilder.bind(vcQueue).to(audioTaskExchange).with("vc");
    }

    @Bean
    public Binding bindConcatQueue(@Qualifier("concatQueue") Queue concatQueue, DirectExchange audioTaskExchange) {
        return BindingBuilder.bind(concatQueue).to(audioTaskExchange).with("concat");
    }

    // Dead Letter Queue Binding 설정
    @Bean
    public Binding bindDLQ(@Qualifier("dlq") Queue dlq, DirectExchange audioDLX) {
        return BindingBuilder.bind(dlq).to(audioDLX).with(DEAD_LETTER_QUEUE);
    }
}
