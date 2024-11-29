package com.fourformance.tts_vc_web.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskConfig {

    public static final String EXCHANGE_NAME = "audioTaskExchange";
    public static final String TTS_QUEUE = "audioTTSQueue";
    public static final String VC_QUEUE = "audioVCQueue";
    public static final String CONCAT_QUEUE = "audioConcatQueue";


    // Direct Exchange 생성
    @Bean
    public DirectExchange audioTaskExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }


    // Queue 생성
    @Bean(name = "ttsQueue")
    public Queue ttsQueue() {
        return new Queue(TTS_QUEUE, true);
    }

    @Bean(name = "vcQueue")
    public Queue vcQueue() {
        return new Queue(VC_QUEUE, true);
    }

    @Bean(name = "concatQueue")
    public Queue concatQueue() {
        return new Queue(CONCAT_QUEUE, true);
    }


    // Binding 설정 (Routing Key 매핑)
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

}
