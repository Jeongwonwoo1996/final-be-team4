package com.fourformance.tts_vc_web.service.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourformance.tts_vc_web.common.config.TaskConfig;
import com.fourformance.tts_vc_web.common.constant.TaskStatusConst;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.Member;
import com.fourformance.tts_vc_web.domain.entity.Project;
import com.fourformance.tts_vc_web.domain.entity.Task;
import com.fourformance.tts_vc_web.dto.common.ConcatMsgDto;
import com.fourformance.tts_vc_web.dto.common.TTSMsgDto;
import com.fourformance.tts_vc_web.dto.common.TaskLoadDto;
import com.fourformance.tts_vc_web.dto.common.VCMsgDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import com.fourformance.tts_vc_web.repository.ProjectRepository;
import com.fourformance.tts_vc_web.repository.TaskRepository;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ConnectionFactory connectionFactory;
    private final TaskProducer taskProducer;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    @Transactional
    public List<TaskLoadDto> getTasksByMemberAndConditions(Long memberId) {

        //Member member = MemberRepository.findById(memberId);
        if(memberId == null) { throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND); }

        List<Task> taskList = taskRepository.findTasksByMemberIdAndConditions(memberId);

        return taskList.stream()
                .map(TaskLoadDto::createTaskLoadDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void terminatePendingTasks(Long memberId) {

        // 1. 존재하는 회원 ID가 있는지 찾기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 회원 Id로 모든 프로젝트 조회
        List<Long> projectIdList = projectRepository.findByMemberId(member.getId());

        // 3. 프로젝트Id 로 completed, terminated가 아닌 모든 상태의 작업 조회
        List<Task> pendingTasks = taskRepository.findByStatus(projectIdList);

        // 4. 상태를 Terminated로 변경
        for (Task task : pendingTasks) {
            task.updateStatus(TaskStatusConst.TERMINATED);
            taskRepository.save(task);
        }

    }

    /**
     * Dead Letter Queue에 있는 실패한 작업들을 원래 큐로 재전송합니다.
     *
     * @return 재시도된 작업 수
     */
    public int restartFailedTasks() {
        int restartedCount = 0;

        try (
                Connection connection = connectionFactory.createConnection();
                Channel channel = connection.createChannel(false)) {

            while (true) {
                // DLQ에서 메시지를 하나 가져옴 (Ack 없이 가져옴)
                GetResponse result = channel.basicGet(TaskConfig.DEAD_LETTER_QUEUE, false);


                if (result == null) {
                    // DLQ에 더 이상 메시지가 없으면 반복 종료 => 로거로 수정
                    System.out.println("DLQ에 더 이상 메시지가 없습니다.");
                    break;
                }

                try {
                    // 메시지 바디 및 속성 가져오기
                    byte[] body = result.getBody(); // payload
                    String jsonString = new String(body);
                    System.out.println("jsonString = " + jsonString);
                    //  {"taskId":12,"detailId":57,"projectId":31,"unitScript":"This is the first detail script.","unitSpeed":1.0,"unitPitch":0.3,"unitVolume":0.5,"unitVoiceStyleId":1}

                    AMQP.BasicProperties properties = result.getProps();

                    // x-death 헤더에서 원래 큐 정보 추출
                    String originalQueue = getOriginalQueue(properties);

                    if (originalQueue == null) {
                        // DLQ에서 메시지 제거하지 않고 다음 메시지로 넘어감
                        continue;
                    }

                    // 메시지에서 taskType 추출
                    String taskType = extractTaskType(originalQueue);

                    // 메시지를 적절한 DTO로 매핑
                    String messageAsString = mapMessageToDto(originalQueue, jsonString);

                    // TaskProducer를 통해 메시지 전송
                    taskProducer.sendTask(taskType, messageAsString);


                    // DLQ에서 메시지 Ack 처리 (DLQ에서 제거)
                    channel.basicAck(result.getEnvelope().getDeliveryTag(), false);

                    restartedCount++;

                } catch (Exception e) {
                    // 처리 중 실패한 메시지는 다시 DLQ에 남김
                    channel.basicNack(result.getEnvelope().getDeliveryTag(), false, true);
                    e.printStackTrace();
                    throw new BusinessException(ErrorCode.DLQ_MESSAGE_PROCESSING_FAILED);
                }
            }
        } catch (IOException | TimeoutException e) {
            throw new BusinessException(ErrorCode.DLQ_RETRY_FAILED);
        }

        return restartedCount;
    }

    /**
     * 원래 큐에 따라 메시지를 적절한 DTO로 매핑 후 직렬화합니다.
     *
     * @param originalQueue 원래 큐 이름
     * @param jsonString    DLQ에서 가져온 메시지 JSON 문자열
     * @return 직렬화된 DTO 문자열
     */
    private String mapMessageToDto(String originalQueue, String jsonString) {
        try {
            switch (originalQueue) {
                case TaskConfig.TTS_QUEUE:
                    TTSMsgDto ttsMsgDto = objectMapper.readValue(jsonString, TTSMsgDto.class);
                    return objectMapper.writeValueAsString(ttsMsgDto);

                case TaskConfig.VC_QUEUE:
                    VCMsgDto vcMsgDto = objectMapper.readValue(jsonString, VCMsgDto.class);
                    return objectMapper.writeValueAsString(vcMsgDto);

                case TaskConfig.CONCAT_QUEUE:
                    ConcatMsgDto concatMsgDto = objectMapper.readValue(jsonString, ConcatMsgDto.class);
                    return objectMapper.writeValueAsString(concatMsgDto);

                default:
                    System.out.println("지원하지 않는 큐 유형입니다: ");
                    throw new BusinessException(ErrorCode.UNKNOWN_TASK_TYPE);
            }
        } catch (JsonProcessingException e) {
            System.out.println("DTO 매핑 중 오류 발생: "+e.getMessage());
            throw new BusinessException(ErrorCode.JSON_PROCESSING_ERROR);
        }
    }

    /**
     * x-death 헤더에서 원래 큐 이름을 추출합니다.
     *
     * @param properties AMQP 메시지 속성
     * @return 원래 큐 이름
     */
    private String getOriginalQueue(AMQP.BasicProperties properties) {
        if (properties.getHeaders() == null || !properties.getHeaders().containsKey("x-death")) {
            return null;
        }

        // x-death 헤더를 List<Map<String, Object>> 형태로 변환
        var xDeathHeader = (java.util.List<java.util.Map<String, Object>>) properties.getHeaders().get("x-death");

        // 가장 최근의 x-death 정보에서 queue 값을 추출
        if (xDeathHeader != null && !xDeathHeader.isEmpty()) {
            var lastDeath = xDeathHeader.get(0);
            return lastDeath.get("queue") != null ? lastDeath.get("queue").toString() : null;
        }

        return null;
    }

    /**
     * 원래 큐 이름에 따라 작업 유형(taskType)을 추출합니다.
     *
     * @param originalQueue 원래 큐 이름
     * @return 작업 유형 (taskType)
     */
    private String extractTaskType(String originalQueue) {
        switch (originalQueue) {
            case TaskConfig.TTS_QUEUE:
                return "AUDIO_TTS";
            case TaskConfig.VC_QUEUE:
                return "AUDIO_VC";
            case TaskConfig.CONCAT_QUEUE:
                return "AUDIO_CONCAT";
            default:
                throw new BusinessException(ErrorCode.UNKNOWN_TASK_TYPE);
        }
    }
}


