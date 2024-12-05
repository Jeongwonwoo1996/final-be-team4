package com.fourformance.tts_vc_web.controller.concat;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.dto.concat.ConcatRequestDetailDto;
import com.fourformance.tts_vc_web.dto.concat.ConcatRequestDto;
import com.fourformance.tts_vc_web.dto.concat.ConcatResponseDto;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.service.concat.ConcatService_team_api;
import com.fourformance.tts_vc_web.service.vc.VCService_team_multi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 컨트롤러 클래스: 오디오 파일 병합 관련 요청을 처리합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/concat")
@Slf4j
public class ConcatController_team_api {

    // 로거 초기화
    private static final Logger LOGGER = Logger.getLogger(ConcatController_team_api.class.getName());

    // 병합 서비스 의존성 주입
    private final ConcatService_team_api concatService;
    private final VCService_team_multi vcService;

    /**
     * 여러 오디오 파일을 업로드하여 병합하는 API 엔드포인트입니다.
     *
     * @param concatRequestDto 병합 요청에 대한 DTO
     * @param files            업로드할 오디오 파일 리스트
     * @param session          현재 HTTP 세션
     * @return 병합 결과를 담은 ResponseDto
     */
    @PostMapping(
            value = "/convert/batch",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    @Operation(
            summary = "오디오 파일 병합",
            description = "여러 오디오 파일을 업로드하고, 파일 사이에 무음을 추가하여 병합된 파일을 생성합니다.",
            tags = {"Audio Concat"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "병합 성공", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = DataResponseDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseDto convertMultipleAudios(
            @RequestPart("concatRequestDto") @Parameter(description = "요청 DTO") ConcatRequestDto concatRequestDto,
            @RequestPart(value = "files", required = false) @Parameter(description = "업로드할 파일들") List<MultipartFile> files,
            HttpSession session
    ) {
        // 요청 데이터 로깅
        LOGGER.info("컨트롤러 메서드 호출됨: " + concatRequestDto);

        // 세션에 memberId 값이 설정되지 않았다면 예외 처리
        if (session.getAttribute("memberId") == null) {
            throw new BusinessException(ErrorCode.SESSION_MEMBER_ID_NOT_SET);
        }

        // 세션에서 memberId 값 추출
        Long memberId = (Long) session.getAttribute("memberId");

        // 1. 유효성 검증: 요청 데이터 및 상세 데이터 확인
        if (concatRequestDto == null ||
                concatRequestDto.getConcatRequestDetails() == null ||
                concatRequestDto.getConcatRequestDetails().isEmpty()) {
            // 잘못된 요청 데이터 로깅
            LOGGER.warning("유효하지 않은 요청 데이터: ConcatRequestDto가 null이거나 비어 있습니다.");
            // 커스텀 예외 발생
            throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA);
        }

        try {
            // 2. 요청 DTO에서 상세 정보 리스트 추출
            List<ConcatRequestDetailDto> details = concatRequestDto.getConcatRequestDetails();

            // 파일이 있는 경우에만 매핑 로직 수행
            if (files != null && !files.isEmpty()) {
                // 업로드된 파일을 맵으로 변환 (파일명 기준)
                Map<String, MultipartFile> fileMap = files.stream()
                        .collect(Collectors.toMap(MultipartFile::getOriginalFilename, file -> file));

                // 요청 DTO의 각 상세 항목에 업로드된 파일 매핑
                for (ConcatRequestDetailDto detail : details) {
                    String localFileName = detail.getLocalFileName();

                    if (localFileName != null && !localFileName.isEmpty()) {
                        // 파일명으로 업로드된 파일 찾기
                        MultipartFile file = fileMap.get(localFileName);

                        if (file != null) {
                            // 매핑 정보 로깅
                            LOGGER.info("매핑 중 - Detail localFileName: " + localFileName + ", 파일명: " + file.getOriginalFilename());
                            // 상세 DTO에 업로드된 파일 설정
                            detail.setSourceAudio(file);
                        } else {
                            // 일치하는 파일이 없을 경우 경고 로그
                            LOGGER.warning("업로드된 파일에서 localFileName과 일치하는 파일을 찾을 수 없습니다: " + localFileName);
                            // 필요한 경우 추가 예외 처리
                        }
                    } else {
                        // localFileName이 없을 경우 S3에 저장된 파일로 처리
                        LOGGER.info("localFileName이 없으므로 S3에 있는 파일로 처리합니다. Detail ID: " + detail.getId());
                        // 추가 작업 불필요
                    }
                }
            }

            // 4. 서비스 로직 호출: 병합 처리 실행
            ConcatResponseDto concatResponse = concatService.convertAllConcatDetails(concatRequestDto, memberId);

            // 병합 성공 로그
            LOGGER.info("오디오 병합 성공");
            // 5. 성공적인 응답 반환
            return DataResponseDto.of(concatResponse);

        } catch (Exception e) {
            // 예외 발생 시 에러 로그 기록
            log.error("오디오 병합 중 오류 발생", e);
            // 커스텀 예외 반환
            throw new BusinessException(ErrorCode.NOT_EXISTS_AUDIO);
        }
    }
}
