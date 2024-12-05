package com.fourformance.tts_vc_web.service.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourformance.tts_vc_web.common.config.TaskConfig;

import com.fourformance.tts_vc_web.common.constant.APIUnitStatusConst;
import com.fourformance.tts_vc_web.common.constant.TaskStatusConst;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.controller.common.SSEController;
import com.fourformance.tts_vc_web.domain.entity.TTSProject;
import com.fourformance.tts_vc_web.domain.entity.Task;
import com.fourformance.tts_vc_web.domain.entity.TaskHistory;
import com.fourformance.tts_vc_web.dto.common.ConcatMsgDto;
import com.fourformance.tts_vc_web.dto.common.TTSMsgDto;
import com.fourformance.tts_vc_web.dto.common.VCMsgDto;
import com.fourformance.tts_vc_web.dto.concat.ConcatMsgDetailDto;
import com.fourformance.tts_vc_web.dto.concat.ConcatResponseDetailDto;
import com.fourformance.tts_vc_web.dto.concat.ConcatResponseDto;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.dto.tts.TTSResponseDetailDto;
import com.fourformance.tts_vc_web.dto.vc.VCDetailResDto;
import com.fourformance.tts_vc_web.repository.TTSProjectRepository;
import com.fourformance.tts_vc_web.repository.TaskHistoryRepository;
import com.fourformance.tts_vc_web.repository.TaskRepository;
import com.fourformance.tts_vc_web.service.concat.AudioProcessingService;
import com.fourformance.tts_vc_web.service.tts.TTSService_TaskJob;
import com.fourformance.tts_vc_web.service.vc.VCService_TaskJob;
import lombok.RequiredArgsConstructor;
import com.rabbitmq.client.Channel;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TaskConsumer {

    @Value("${upload.dir}")
    private String uploadDir;

    private final ObjectMapper objectMapper;
    private final TaskRepository taskRepository;
    private final TaskHistoryRepository historyRepository;
    private final TTSService_TaskJob ttsService;
    private final TTSProjectRepository ttsProjectRepository;
    private final VCService_TaskJob vcService;
    private final SseEmitterService sseService;
    private final AudioProcessingService audioProcessingService;
    private final S3Service s3Service;


    /**
     * TTS 작업 처리: 큐에서 작업을 꺼내 TTS 작업 처리
     *
     */
    @RabbitListener(queues = TaskConfig.TTS_QUEUE, ackMode = "MANUAL")
    public void handleTTSTask(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        Long projectId = -1L;
        Long detailId  = -1L;
        Long taskId    = -1L;

        try {
            // meassage(String) -> TTSMsgDto 로 역직렬화
            TTSMsgDto ttsMsgDto = objectMapper.readValue(message, TTSMsgDto.class);
            projectId = ttsMsgDto.getProjectId();
            detailId  = ttsMsgDto.getDetailId();
            taskId    = ttsMsgDto.getTaskId();

            if(projectId == -1L || detailId == -1L || taskId == -1) { throw new BusinessException(ErrorCode.JSON_PROCESSING_ERROR); }

            // 상태가 TERMINATED인 작업은 처리하지 않고 ACK 후 종료
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.TASK_NOT_FOUND));

            if (task.getTaskStatusConst() == TaskStatusConst.TERMINATED) {
                channel.basicAck(tag, false); // 메시지 큐에서 제거
                return;
            }

            System.out.println("taskId = " + taskId);
            // 상태 업데이트
            updateStatus(task.getId(), TaskStatusConst.RUNNABLE, "작업 시작");
            System.out.println("=============실행됑라라ㅏ랄");
            sseService.sendToClient(projectId, "TTS 작업이 시작되었습니다.");

            // TTS 작업
            TTSProject ttsProject = ttsProjectRepository.findById(projectId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

            Map<String, String> fileUrlMap = ttsService.processTtsDetail(ttsMsgDto, ttsProject);
            String fileUrl = fileUrlMap.get("fileUrl");

            // TTSResponseDetailDto 생성 및 추가
            TTSResponseDetailDto responseDetail = TTSResponseDetailDto.builder()
                    .id(detailId)
                    .projectId(ttsMsgDto.getProjectId())
                    .unitScript(ttsMsgDto.getUnitScript())
                    .unitSpeed(ttsMsgDto.getUnitSpeed())
                    .unitPitch(ttsMsgDto.getUnitPitch())
                    .unitVolume(ttsMsgDto.getUnitVolume())
                    .UnitVoiceStyleId(ttsMsgDto.getUnitVoiceStyleId())
                    .fileUrl(fileUrl) // 처리된 URL 삽입
                    .apiUnitStatus(APIUnitStatusConst.SUCCESS)
                    .build();

            ResponseDto response =  DataResponseDto.of(responseDetail);


            // 메시지 처리 완료 시 (1. RabbitMQ에 ACK 전송, 2. SSE로 전달, 3. 상태값 변환(완료))
            channel.basicAck(tag, false);

            updateStatus(taskId, TaskStatusConst.COMPLETED, "작업 완료");
            sseService.sendToClient(projectId, response);

        } catch (JsonProcessingException JsonError) { // JSON 파싱 에러 처리

            updateStatus(taskId, TaskStatusConst.FAILED, "작업 실패");
            sseService.sendToClient(projectId, null);
            throw new BusinessException(ErrorCode.JSON_PROCESSING_ERROR);

        } catch (Exception e) {

            try { // Dead Letter Queue로 메시지 전달
                channel.basicNack(tag, false, false);
            } catch (IOException ioException) {
                throw new BusinessException(ErrorCode.FAILED_TASK_PROCESSING_ERROR);
            } finally {
                updateStatus(taskId, TaskStatusConst.FAILED, "작업 실패");
                sseService.sendToClient(projectId, null);
            }
        }
    }


    /**
     * VC 작업 처리: 큐에서 작업을 꺼내 VC 작업 처리
     *
     */
    @RabbitListener(queues = TaskConfig.VC_QUEUE, ackMode = "MANUAL")
    public void handleVCTask(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        Long projectId = -1L;
        Long detailId  = -1L;
        Long taskId    = -1L;

        try {
            // meassage(String) -> VCMsgDto 로 역직렬화
            VCMsgDto vcMsgDto = objectMapper.readValue(message, VCMsgDto.class);
            projectId = vcMsgDto.getProjectId();
            detailId  = vcMsgDto.getDetailId();
            taskId    = vcMsgDto.getTaskId();

            if(projectId == -1L || detailId == -1L || taskId == -1) { throw new BusinessException(ErrorCode.JSON_PROCESSING_ERROR); }

            // 상태가 TERMINATED인 작업은 처리하지 않고 ACK 후 종료
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.TASK_NOT_FOUND));

            if (task.getTaskStatusConst() == TaskStatusConst.TERMINATED) {
                channel.basicAck(tag, false); // 메시지 큐에서 제거
                return;
            }

            // 상태 업데이트
            updateStatus(taskId, TaskStatusConst.RUNNABLE, "작업 시작");
            sseService.sendToClient(projectId, "VC 작업이 시작되었습니다.");

            // VC 작업
            VCDetailResDto vcDetailsRes = vcService.processSourceFile(vcMsgDto);

            ResponseDto response =  DataResponseDto.of(vcDetailsRes);

            // 메시지 처리 완료 시 (1. RabbitMQ에 ACK 전송, 2. SSE로 전달, 3. 상태값 변환(완료))
            channel.basicAck(tag, false);
            updateStatus(taskId, TaskStatusConst.COMPLETED, "작업 완료");
            sseService.sendToClient(projectId, response);


        }catch (JsonProcessingException JsonError) { // JSON 파싱 에러 처리

            updateStatus(taskId, TaskStatusConst.FAILED, "작업 실패");
            sseService.sendToClient(projectId, null);
            throw new BusinessException(ErrorCode.JSON_PROCESSING_ERROR);

        } catch (Exception e) {

            try { // Dead Letter Queue로 메시지 전달
                channel.basicNack(tag, false, false);
            } catch (IOException ioException) {
                throw new BusinessException(ErrorCode.FAILED_TASK_PROCESSING_ERROR);
            } finally {
                updateStatus(taskId, TaskStatusConst.FAILED, "작업 실패");
                sseService.sendToClient(projectId, null);
            }
        }
    }

    /**
     * Concat 작업 처리: 큐에서 작업을 꺼내 Concat 작업 처리
     *
     */
    @RabbitListener(queues = TaskConfig.CONCAT_QUEUE, ackMode = "MANUAL")
    public void handleConcatTask(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        Long projectId = -1L;
        Long taskId    = -1L;

        try {
            // meassage(String) -> VCMsgDto 로 역직렬화
            ConcatMsgDto concatMsgDto = objectMapper.readValue(message, ConcatMsgDto.class);
            projectId = concatMsgDto.getProjectId();
            taskId    = concatMsgDto.getTaskId();

            if(projectId == -1L || taskId == -1) { throw new BusinessException(ErrorCode.JSON_PROCESSING_ERROR); }

            // 상태가 TERMINATED인 작업은 처리하지 않고 ACK 후 종료
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.TASK_NOT_FOUND));

            if (task.getTaskStatusConst() == TaskStatusConst.TERMINATED) {
                channel.basicAck(tag, false); // 메시지 큐에서 제거
                return;
            }

            // 상태 업데이트
            updateStatus(taskId, TaskStatusConst.RUNNABLE, "작업 시작");
            sseService.sendToClient(projectId, "Concat 작업이 시작되었습니다.");


            // Concat 작업 시작
            // 2. S3에서 파일 다운로드 및 침묵 파일 생성
            List<String> savedFilePaths = new ArrayList<>();
            List<String> silenceFilePaths = new ArrayList<>();
            String mergedFilePath = null;

            for (ConcatMsgDetailDto detail : concatMsgDto.getConcatMsgDetailDtos()) {
                if (detail.getSrcUrl() != null) {
                    String savedFilePath = s3Service.downloadFileFromS3(detail.getSrcUrl(), uploadDir);
                    savedFilePaths.add(savedFilePath);

                    String silenceFilePath = audioProcessingService.createSilenceFile(detail.getEndSilence().longValue(), uploadDir);
                    if (silenceFilePath != null) silenceFilePaths.add(silenceFilePath);
                } else {
                    // src url 이 없을 경우 에러 처리
                }
            }

            // 3. 병합된 파일 생성
            mergedFilePath = audioProcessingService.mergeAudioFilesWithSilence(savedFilePaths, silenceFilePaths, uploadDir);

            // 4. 병합된 파일을 S3에 업로드 후 URL 반환
            String mergedFileUrl = s3Service.uploadConcatSaveFile(audioProcessingService.convertToMultipartFile(mergedFilePath), concatMsgDto.getMemberId(), projectId);

            ConcatResponseDto concatResponseDto = ConcatResponseDto.builder()
                    .projectId(concatMsgDto.getProjectId())
                    .globalFrontSilenceLength(concatMsgDto.getGlobalFrontSilenceLength())
                    .outputConcatAudios(Collections.singletonList(mergedFileUrl))
                    .build();


            ResponseDto response =  DataResponseDto.of(concatResponseDto);

            // 메시지 처리 완료 시 (1. RabbitMQ에 ACK 전송, 2. SSE로 전달, 3. 상태값 변환(완료))
            channel.basicAck(tag, false);
            updateStatus(taskId, TaskStatusConst.COMPLETED, "작업 완료");
            sseService.sendToClient(projectId, response);


        }catch(Exception e){
            try { // Dead Letter Queue로 메시지 전달
                channel.basicNack(tag, false, false);
            } catch (IOException ioException) {
                throw new BusinessException(ErrorCode.FAILED_TASK_PROCESSING_ERROR);
            } finally {
                updateStatus(taskId, TaskStatusConst.FAILED, "작업 실패");
                sseService.sendToClient(projectId, null);
            }
        }
    }


    /**
     * 작업 상태 update: 작업 상태 업데이트 처리
     *
     */
    @Transactional
    public void updateStatus(Long taskId, TaskStatusConst newStatusConst, String msg) {
        System.out.println("==============업데이트 스테이터스 메서드 초입");
        // 1. Task 엔티티 조회 및 상태 update
        Task task = taskRepository.findById(taskId)
                 .orElseThrow(() ->  new BusinessException(ErrorCode.TASK_NOT_FOUND));
             task.updateStatus(newStatusConst);
         taskRepository.save(task);

        // 2. 최신 TaskHistory 조회
        TaskHistory latestHistory = historyRepository.findLatestTaskHistoryByTaskId(task.getId());

        // 3. 최신 이력이 없는 경우 처리 (최초 TaskHistory 생성)
        TaskStatusConst oldStatus = latestHistory != null ? latestHistory.getNewStatus() : TaskStatusConst.NEW;

        // 4. 새로운 TaskHistory 생성
        TaskHistory taskHistory = TaskHistory.createTaskHistory(task, oldStatus, newStatusConst, msg);

        // 5. TaskHistory 저장
        historyRepository.save(taskHistory);
    }

}
