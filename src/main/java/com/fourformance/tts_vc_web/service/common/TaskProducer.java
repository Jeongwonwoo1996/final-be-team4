package com.fourformance.tts_vc_web.service.common;

import com.fourformance.tts_vc_web.common.config.TaskConfig;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskProducer {

    private final RabbitTemplate rabbitTemplate;

    public TaskProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    // 오디오 작업 메시지 전송
    public void sendTask(String taskType, String message) {
        String routingKey = getRoutingKey(taskType);
        rabbitTemplate.convertAndSend(TaskConfig.EXCHANGE_NAME, routingKey, message);
        System.out.println("Sent message: [" + message + "] to routing key: " + routingKey);
    }

    // 작업 유형에 따른 Routing Key 반환
    private String getRoutingKey(String taskType) {
        switch (taskType) {
            case "AUDIO_TTS":
                return "tts";
            case "AUDIO_VC":
                return "vc";
            case "AUDIO_CONCAT":
                return "concat";
            default:
                throw new BusinessException(ErrorCode.UNKNOWN_TASK_TYPE);
        }
    }
}
