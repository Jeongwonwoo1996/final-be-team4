package com.fourformance.tts_vc_web.controller.tts;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.dto.tts.TTSRequestDto;
import com.fourformance.tts_vc_web.dto.tts.TTSResponseDto;
import com.fourformance.tts_vc_web.service.tts.TTSService_team_api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TTS API 컨트롤러
 * 텍스트 데이터를 음성 파일로 변환하는 API를 제공합니다.
 */
@Tag(name = "tts-controller-_team-_api", description = "텍스트를 음성 파일로 변환하는 API")
@RestController
@RequestMapping("/tts")
public class TTSController_team_api {

    private static final Logger LOGGER = Logger.getLogger(TTSController_team_api.class.getName()); // 로깅을 위한 Logger

    private final TTSService_team_api ttsService; // TTS 변환 로직을 처리하는 서비스

    /**
     * 생성자: 서비스 의존성을 주입받아 초기화
     *
     * @param ttsService TTS 변환 서비스
     */
    public TTSController_team_api(TTSService_team_api ttsService) {
        this.ttsService = ttsService;
    }

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
    public ResponseDto convertBatchTexts(@RequestBody TTSRequestDto ttsRequestDto, HttpSession session) {
        LOGGER.info("컨트롤러 호출됨: " + ttsRequestDto);

        // 세션에 memberId 값이 설정되지 않았다면 예외 처리
        if (session.getAttribute("memberId") == null) {
            throw new BusinessException(ErrorCode.SESSION_MEMBER_ID_NOT_SET);
        }

        // 세션에 memberId 값 설정
        Long memberId = (Long) session.getAttribute("memberId");

        // 유효성 검증: 요청 데이터가 null이거나 텍스트 세부사항 리스트가 비어있는 경우 예외 처리
        if (ttsRequestDto == null || ttsRequestDto.getTtsDetails() == null || ttsRequestDto.getTtsDetails().isEmpty()) {
            LOGGER.warning("유효하지 않은 요청 데이터"); // 잘못된 요청 데이터 로깅
            throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA); // 커스텀 예외 발생
        }

        // 요청 데이터 유효성 검사
        validateRequestData(ttsRequestDto);

        try {
            // TTS 변환 처리
             System.out.println("=========ttsRequestDto.toString() = " + ttsRequestDto.toString());
            TTSResponseDto ttsResponseDto = ttsService.convertAllTtsDetails(ttsRequestDto, 1L);


            // 변환 결과가 비어있으면 실패로 간주
            if (ttsResponseDto.getTtsDetails().isEmpty()) {
                LOGGER.warning("TTS 변환 실패: 디테일 데이터가 없습니다.");
                throw new BusinessException(ErrorCode.TTS_CREATE_FAILED);
            }

            LOGGER.info("TTS 변환 성공");
            // 변환 성공 응답 반환
            return DataResponseDto.of(ttsResponseDto);

        } catch (BusinessException e) {
            // 비즈니스 로직 예외 처리
            LOGGER.log(Level.WARNING, "비즈니스 예외 발생", e);
            throw e;
        } catch (Exception e) {
            // 시스템 예외 처리
            LOGGER.log(Level.SEVERE, "TTS 변환 중 시스템 예외 발생", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_TTS_ERROR);
        }
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
