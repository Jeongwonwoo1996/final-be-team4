package com.fourformance.tts_vc_web.service.common;

import com.fourformance.tts_vc_web.common.config.TaskConfig;
import com.fourformance.tts_vc_web.common.constant.TaskStatusConst;
import com.fourformance.tts_vc_web.domain.entity.Task;
import com.fourformance.tts_vc_web.repository.TaskHistoryRepository;
import com.fourformance.tts_vc_web.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TaskConsumer {

    private final TaskRepository taskRepository;
    private final TaskHistoryRepository taskHistoryRepository;

    @RabbitListener(queues = TaskConfig.TTS_QUEUE)
    public void handleTTSTask(String message) {
        System.out.println("TTS audio task : " + message);

        // 상태 업데이트
        // Task task = 받아온거;
        // task.updateStatus(TaskStatusConst.RUNNABLE);


        // TTS 생성 로직 구현
        try{

        }catch(Exception e){
            // 실패 상태로 업데이트
            System.out.println("TTS 처리 실패"+e);
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
