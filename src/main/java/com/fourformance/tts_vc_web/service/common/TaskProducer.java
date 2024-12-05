package com.fourformance.tts_vc_web.service.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourformance.tts_vc_web.common.config.TaskConfig;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper; // JSON 직렬화를 위한 ObjectMapper


    // 오디오 작업 메시지 전송
    public void sendTask(String taskType, Object messageDto) {
        try {
            String routingKey = getRoutingKey(taskType);

            String message = objectMapper.writeValueAsString(messageDto); // DTO를 JSON으로 직렬화

            rabbitTemplate.convertAndSend(TaskConfig.EXCHANGE_NAME, routingKey, message);

            System.out.println("Sent message: [" + message + "] to routing key: " + routingKey);
        } catch (Exception e) {
            throw new RuntimeException("메시지 전송 중 오류 발생", e);
        }
    }

    // 작업 유형에 따른 Routing Key 반환
    public String getRoutingKey(String taskType) {
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
