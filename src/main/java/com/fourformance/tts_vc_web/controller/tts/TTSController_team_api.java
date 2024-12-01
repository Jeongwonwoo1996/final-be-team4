package com.fourformance.tts_vc_web.controller.tts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourformance.tts_vc_web.common.constant.ProjectType;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.Project;
import com.fourformance.tts_vc_web.domain.entity.Task;
import com.fourformance.tts_vc_web.dto.common.TTSMsgDto;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.dto.tts.TTSRequestDetailDto;
import com.fourformance.tts_vc_web.dto.tts.TTSRequestDto;
import com.fourformance.tts_vc_web.dto.tts.TTSResponseDto;
import com.fourformance.tts_vc_web.repository.ProjectRepository;
import com.fourformance.tts_vc_web.repository.TaskRepository;
import com.fourformance.tts_vc_web.service.common.TaskProducer;
import com.fourformance.tts_vc_web.service.tts.TTSService_team_api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TTS API 컨트롤러
 * 텍스트 데이터를 음성 파일로 변환하는 API를 제공합니다.
 */
@Tag(name = "tts-controller-_team-_api", description = "텍스트를 음성 파일로 변환하는 API")
@RestController
@RequestMapping("/tts")
@RequiredArgsConstructor
public class TTSController_team_api {

    private static final Logger LOGGER = Logger.getLogger(TTSController_team_api.class.getName()); // 로깅을 위한 Logger

    private final TTSService_team_api ttsService; // TTS 변환 로직을 처리하는 서비스
    private final TaskRepository taskRepository;
    private final TaskProducer taskProducer;
    private final ProjectRepository projectRepository;


    /**
     * 텍스트 목록을 음성 파일로 변환하는 API 엔드포인트
     *
     * @param ttsRequestDto 변환 요청 데이터 (프로젝트 정보 및 텍스트 디테일 포함)
     * @return 변환 결과 데이터 (음성 파일 URL 및 상태 정보 포함)
     */
    @Operation(summary = "TTS 배치 변환", description = "주어진 텍스트 목록을 Google TTS API를 사용하여 음성 파일로 변환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TTS 변환 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/convert/batch")
    public ResponseDto convertBatchTexts(
            @RequestBody TTSRequestDto ttsRequestDto,
            @RequestParam(required = false, defaultValue = "false") Boolean queue,
            HttpSession session) {

        session.setAttribute("memberId",1L);

        // 세션에 memberId 값이 설정되지 않았다면 예외 처리
        if (session.getAttribute("memberId") == null) {
            throw new BusinessException(ErrorCode.SESSION_MEMBER_ID_NOT_SET);
        }

        // 세션에 memberId 값 설정
        Long memberId = (Long) session.getAttribute("memberId");

        if(queue){
            // 비동기 처리: RabbitMQ로 메시지 전송

            // 1. 프로젝트&디테일 저장
            // 2. 저장된 값으로 음성변환 실행(해당 서비스에서 큐 작업 처리)
            Project findProject = projectRepository.findById(ttsRequestDto.getProjectId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT));

            List<TTSRequestDetailDto> ttsDetails = ttsRequestDto.getTtsDetails();
            if (ttsDetails == null || ttsDetails.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA);
            }

            for (TTSRequestDetailDto detail : ttsDetails) {
                String detailJson = convertDetailToJson(detail);

                // Task 생성 및 저장
                Task task = Task.createTask(findProject, ProjectType.TTS, detailJson);
                taskRepository.save(task); // 저장 후 ID 생성

                // TTSMsgDto 생성 (taskId 포함)
                TTSMsgDto message = convertToTTSMsgDto(detail, memberId, task.getId());

                taskProducer.sendTask("AUDIO_TTS", message);
            }

            return DataResponseDto.of("TTS 작업이 큐에 추가되었습니다.");
        }else{ // 동기 처리, 나중에 삭제 할 코드
            // 유효성 검증: 요청 데이터가 null이거나 텍스트 세부사항 리스트가 비어있는 경우 예외 처리
            if (ttsRequestDto == null || ttsRequestDto.getTtsDetails() == null || ttsRequestDto.getTtsDetails().isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA); // 커스텀 예외 발생
            }

            // 요청 데이터 유효성 검사
            validateRequestData(ttsRequestDto);


            // TTS 변환 처리
            TTSResponseDto ttsResponseDto = ttsService.convertAllTtsDetails(ttsRequestDto, memberId);

            // 변환 결과가 비어있으면 실패로 간주
            if (ttsResponseDto.getTtsDetails().isEmpty()) {
                throw new BusinessException(ErrorCode.TTS_CREATE_FAILED);
            }

            // 변환 성공 응답 반환
            return DataResponseDto.of(ttsResponseDto);
        }
    }

    // 추후에 서비스 로직으로 빼기 - 유람
    private String convertDetailToJson(TTSRequestDetailDto detail) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(detail);
        } catch (JsonProcessingException e) {
            // JSON 직렬화 실패 시 커스텀 예외 던지기
            throw new BusinessException(ErrorCode.JSON_PROCESSING_ERROR);
        }
    }
    private TTSMsgDto convertToTTSMsgDto(TTSRequestDetailDto detail, Long memberId, Long taskId) {
        return TTSMsgDto.builder()
                .ttsDetail(detail)       // `TTSRequestDetailDto`를 직접 매핑
                .memberId(memberId)      // 요청한 회원 ID
                .taskId(taskId)          // 생성된 Task ID
                .timestamp(LocalDateTime.now()) // 현재 시간
                .build();
    }

    /**
     * 요청 데이터 유효성 검사
     *
     * @param ttsRequestDto 요청 데이터
     * @throws BusinessException 잘못된 요청 데이터인 경우 예외 발생
     */
    private void validateRequestData(TTSRequestDto ttsRequestDto) {
        if (ttsRequestDto == null) {
            LOGGER.warning("요청 데이터가 null입니다.");
            throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA);
        }
        if (ttsRequestDto.getTtsDetails() == null || ttsRequestDto.getTtsDetails().isEmpty()) {
            LOGGER.warning("요청 데이터에 텍스트 디테일이 없습니다.");
            throw new BusinessException(ErrorCode.INVALID_REQUEST_TEXT_DETAIL_DATA);
        }
        LOGGER.info("요청 데이터 유효성 검사 통과");
    }
}
