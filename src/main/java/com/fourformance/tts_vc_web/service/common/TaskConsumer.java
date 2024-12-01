package com.fourformance.tts_vc_web.service.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourformance.tts_vc_web.common.config.TaskConfig;

import com.fourformance.tts_vc_web.common.constant.APIUnitStatusConst;
import com.fourformance.tts_vc_web.common.constant.TaskStatusConst;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.TTSProject;
import com.fourformance.tts_vc_web.domain.entity.Task;
import com.fourformance.tts_vc_web.domain.entity.TaskHistory;
import com.fourformance.tts_vc_web.dto.common.TTSMsgDto;
import com.fourformance.tts_vc_web.dto.common.VCMsgDto;
import com.fourformance.tts_vc_web.dto.tts.TTSResponseDetailDto;
import com.fourformance.tts_vc_web.dto.tts.TTSResponseDto;
import com.fourformance.tts_vc_web.repository.TTSProjectRepository;
import com.fourformance.tts_vc_web.repository.TaskHistoryRepository;
import com.fourformance.tts_vc_web.repository.TaskRepository;
import com.fourformance.tts_vc_web.service.tts.TTSService_TaskJob;
import com.fourformance.tts_vc_web.service.tts.TTSService_team_api;
import lombok.RequiredArgsConstructor;
import com.rabbitmq.client.Channel;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TaskConsumer {

    private final ObjectMapper objectMapper;
    private final TaskRepository taskRepository;
    private final TaskHistoryRepository historyRepository;
    private final TTSService_TaskJob ttsService;
    private final TTSProjectRepository ttsProjectRepository;

    /**
     * TTS 작업 처리: 큐에서 작업을 꺼내 TTS 작업 처리
     *
     */
    @RabbitListener(queues = TaskConfig.TTS_QUEUE, ackMode = "MANUAL")
    public TTSResponseDetailDto handleTTSTask(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        System.out.println("TTS audio task : " + message);

        try {
            // meassage(String) -> TTSMsgDto 로 역직렬화
            TTSMsgDto ttsMsgDto = objectMapper.readValue(message, TTSMsgDto.class);

            // 상태 업데이트
            Task task = taskRepository.findByNameInJson(ttsMsgDto.getTtsDetail().getId());
            TaskHistory latestHistory = historyRepository.findLatestTaskHistoryByTaskId(task.getId());
            TaskHistory taskHistory   = TaskHistory.createTaskHistory(task, latestHistory.getNewStatus(), TaskStatusConst.RUNNABLE, "작업 시작");
            //TaskHistoryRepository.save(taskHistory)


            // TTS 작업
            // ttsService.processTtsDetail 매개변수 수정하기 (TTSRequestDto -> ttsMsgDto로 변경)
            // 반환값 처리하기
            TTSProject ttsProject = ttsProjectRepository.findById(ttsMsgDto.getProjectId())
                    .orElseThrow(() -> { throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND); });
            Map<String, String> fileUrlMap = ttsService.processTtsDetail(ttsMsgDto, ttsProject);
            String fileUrl = fileUrlMap.get("fileUrl");

            // TTSResponseDetailDto 생성 및 추가
            TTSResponseDetailDto responseDetail = TTSResponseDetailDto.builder()
                    .id(ttsMsgDto.getDetailId())
                    .projectId(ttsMsgDto.getProjectId())
                    .unitScript(ttsMsgDto.getUnitScript())
                    .unitSpeed(ttsMsgDto.getUnitSpeed())
                    .unitPitch(ttsMsgDto.getUnitPitch())
                    .unitVolume(ttsMsgDto.getUnitVolume())
                    .UnitVoiceStyleId(ttsMsgDto.getUnitVoiceStyleId())
                    .fileUrl(fileUrl) // 처리된 URL 삽입
                    .apiUnitStatus(APIUnitStatusConst.SUCCESS)
                    .build();



            // 메시지 처리 완료 시 1. RabbitMQ에 ACK 전송, 2. SSE로 전달, 3. 상태값 변환(완료)
            channel.basicAck(tag, false);

            Task newTask = taskRepository.findByNameInJson(ttsMsgDto.getTtsDetail().getId());
            TaskHistory latestHistory2 = historyRepository.findLatestTaskHistoryByTaskId(newTask.getId());
            TaskHistory newTaskHistory   = TaskHistory.createTaskHistory(task, latestHistory2.getNewStatus(), TaskStatusConst.RUNNABLE, "작업 시작");

            return responseDetail;


            // 예외를 강제로 발생
            // throw new RuntimeException("Processing failed for TTS task");


        } catch (JsonProcessingException e) { // 상태값 변환(실패)
            // JSON 파싱 에러 처리
            System.err.println("Failed to parse message: " + e.getMessage());


        } catch (Exception e) { // 상태값 변환(실패)
            System.err.println("Message processing failed: " + e.getMessage());
            try {
                // Dead Letter로 메시지 전달
                channel.basicNack(tag, false, false); // 메시지를 다시 처리하지 않고 DLQ로 이동
            } catch (IOException ioException) {
                System.err.println("Error while rejecting the message: " + ioException.getMessage());
            }
        }
        return null;
    }

    /**
     * VC 작업 처리: 큐에서 작업을 꺼내 VC 작업 처리
     *
     */
    @RabbitListener(queues = TaskConfig.VC_QUEUE)
    public void handleVCTask(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        System.out.println("VC audio task : " + message);

        // 처리 로직 구현
        try{
            // meassage(String) -> TTSMsgDto 로 역직렬화
            VCMsgDto vcMsgDto = objectMapper.readValue(message, VCMsgDto.class);

            // 상태 업데이트
            Task task = taskRepository.findByNameInJson(vcMsgDto.getDetailId());
            TaskHistory latestHistory = historyRepository.findLatestTaskHistoryByTaskId(task.getId());
            TaskHistory taskHistory   = TaskHistory.createTaskHistory(task, latestHistory.getNewStatus(), TaskStatusConst.RUNNABLE, "작업 시작");




        }catch(Exception e){
            // 실패 상태로 업데이트
            System.out.println("TTS 처리 실패"+e);
        }
    }

    /**
     * Concat 작업 처리: 큐에서 작업을 꺼내 Concat 작업 처리
     *
     */
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
