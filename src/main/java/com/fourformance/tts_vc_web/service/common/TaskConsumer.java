package com.fourformance.tts_vc_web.service.common;

import com.fourformance.tts_vc_web.common.config.TaskConfig;

import lombok.RequiredArgsConstructor;
import com.rabbitmq.client.Channel;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TaskConsumer {


    @RabbitListener(queues = TaskConfig.TTS_QUEUE, ackMode = "MANUAL")
    public void handleTTSTask(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            System.out.println("TTS audio task : " + message);

            // 예외를 강제로 발생
            throw new RuntimeException("Processing failed for TTS task");


        } catch (Exception e) {
            System.err.println("Message processing failed: " + e.getMessage());
            try {
                // Dead Letter로 메시지 전달
                channel.basicNack(tag, false, false); // 메시지를 다시 처리하지 않고 DLQ로 이동
            } catch (IOException ioException) {
                System.err.println("Error while rejecting the message: " + ioException.getMessage());
            }
        }
    }

    @RabbitListener(queues = TaskConfig.VC_QUEUE)
    public void handleVCTask(String message) {
        System.out.println("VC audio task : " + message);
        // 처리 로직 구현
        try{

        }catch(Exception e){
            // 실패 상태로 업데이트
            System.out.println("TTS 처리 실패"+e);
        }
    }

    @RabbitListener(queues = TaskConfig.CONCAT_QUEUE)
    public void handleConcatTask(String message) {
        System.out.println("Concat audio task : " + message);
        // 처리 로직 구현
        // TTS 생성 로직 구현
        try{

        }catch(Exception e){
            // 실패 상태로 업데이트
            System.out.println("TTS 처리 실패"+e);
        }
    }
}
