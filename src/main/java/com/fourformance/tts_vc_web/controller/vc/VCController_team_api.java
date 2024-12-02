package com.fourformance.tts_vc_web.controller.vc;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.dto.vc.VCDetailResDto;
import com.fourformance.tts_vc_web.dto.vc.VCSaveRequestDto;
import com.fourformance.tts_vc_web.service.vc.VCService_team_api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Voice Conversion API 컨트롤러
 * 소스 및 타겟 오디오 파일 처리 및 Voice ID 생성 기능을 제공합니다.
 */
@Tag(name = "Voice Conversion API", description = "Voice Conversion 관련 기능을 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/vc")
public class VCController_team_api {

    private static final Logger LOGGER = Logger.getLogger(VCController_team_api.class.getName());
    private final VCService_team_api vcService;

    /**
     * VC 프로젝트 처리 엔드포인트
     *
     * @param VCSaveRequestDto 프로젝트 저장 및 처리 요청 데이터
     * @param files     소스 오디오 파일 리스트
     * @param session   현재 HTTP 세션 (회원 정보 저장)
     * @return VCDetailResDto의 리스트
     */
    @Operation(summary = "VC 프로젝트 처리", description = "소스/타겟 오디오 파일 처리 및 Voice ID 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "VC 프로젝트 처리 성공")
    })
    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto processVCProject(
            @RequestPart("VCSaveRequestDto") VCSaveRequestDto VCSaveRequestDto,
            @RequestPart("files") List<MultipartFile> files,
            HttpSession session) {
        LOGGER.info("VC 프로젝트 처리 요청 시작");

        // 세션에 memberId 값이 설정되지 않았다면 예외 처리
        if (session.getAttribute("memberId") == null) {
            throw new BusinessException(ErrorCode.SESSION_MEMBER_ID_NOT_SET);
        }

        // 세션에서 memberId 가져오기
        Long memberId = (Long) session.getAttribute("memberId");

        // 요청 데이터 유효성 검사
        validateRequestData(VCSaveRequestDto);

        try {
            // VC 프로젝트 처리
            List<VCDetailResDto> response = vcService.processVCProject(VCSaveRequestDto, files, memberId);

            LOGGER.info("VC 프로젝트 처리 성공");
            return DataResponseDto.of(response);

        } catch (BusinessException e) {
            // 비즈니스 로직 예외 처리
            LOGGER.log(Level.WARNING, "비즈니스 예외 발생", e);
            throw e;
        } catch (Exception e) {
            // 시스템 예외 처리
            LOGGER.log(Level.SEVERE, "VC 프로젝트 처리 중 시스템 예외 발생", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_VC_ERROR);
        }
    }

    /**
     * 요청 데이터 유효성 검사
     *
     * @param vcSaveRequestDto 요청 데이터
     * @throws BusinessException 잘못된 요청 데이터인 경우 예외 발생
     */
    private void validateRequestData(VCSaveRequestDto vcSaveRequestDto) {
        if (vcSaveRequestDto == null) {
            LOGGER.warning("요청 데이터가 null입니다.");
            throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA);
        }
        if (vcSaveRequestDto.getTrgFiles() == null || vcSaveRequestDto.getTrgFiles().isEmpty()) {
            LOGGER.warning("요청 데이터에 타겟 오디오 파일이 없습니다.");
            throw new BusinessException(ErrorCode.INVALID_REQUEST_FILE_DATA);
        }
        LOGGER.info("요청 데이터 유효성 검사 통과");
    }
}
