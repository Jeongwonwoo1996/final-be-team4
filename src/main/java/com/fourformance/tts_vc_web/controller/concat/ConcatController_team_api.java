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
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concat")
@Slf4j
public class ConcatController_team_api {

    private static final Logger LOGGER = Logger.getLogger(ConcatController_team_api.class.getName()); // 로거 초기화

    private final ConcatService_team_api concatService; // 병합 서비스 의존성 주입
    private final VCService_team_multi vcService;

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
            @RequestPart(value = "files", required = false) @Parameter(description = "업로드할 파일들") List<MultipartFile> files, HttpSession session
    ) {
        LOGGER.info("컨트롤러 메서드 호출됨: " + concatRequestDto); // 요청 데이터 로깅

        // 세션에 memberId 값이 설정되지 않았다면 예외 처리
        if (session.getAttribute("memberId") == null) {
            throw new BusinessException(ErrorCode.SESSION_MEMBER_ID_NOT_SET);
        }

        // 세션에 memberId 값 설정
        Long memberId = (Long) session.getAttribute("memberId");


        // 1. 유효성 검증: 요청 데이터 및 상세 데이터 확인
        if (concatRequestDto == null ||
                concatRequestDto.getConcatRequestDetails() == null ||
                concatRequestDto.getConcatRequestDetails().isEmpty()) {
            LOGGER.warning("유효하지 않은 요청 데이터: ConcatRequestDto가 null이거나 비어 있습니다."); // 잘못된 요청 데이터 로깅
            throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA); // 커스텀 예외 발생
        }

        try {
            // 2. 파일 수와 요청 DTO의 상세 정보 수가 동일한지 확인
            List<ConcatRequestDetailDto> details = concatRequestDto.getConcatRequestDetails();


//            if (details.size() != files.size()) {
//                LOGGER.warning("파일 수와 요청 DTO의 상세 데이터 수가 일치하지 않음");
//                throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA);
//            }
//
//            // 3. 요청 DTO의 각 상세 항목에 업로드된 파일 매핑
//            for (int i = 0; i < details.size(); i++) {
//                ConcatRequestDetailDto detail = details.get(i);
////                MultipartFile file = files.get(i);
//                MultipartFile file = vcService.findMultipartFileByName(files, detail.getLocalFileName());
//
//                // 예를 들어, 파일명과 detail의 정보가 일치하는지 확인
//                LOGGER.info("매핑 중 - Detail localFileName: " + detail.getLocalFileName() + ", 파일명: " + file.getOriginalFilename());
//
//                detail.setSourceAudio(file);
//
//            }

            // 파일이 있는 경우에만 매핑 로직 수행
            if (files != null && !files.isEmpty()) {
                // 2. 요청 DTO의 각 상세 항목에 업로드된 파일 매핑
                for (int i = 0; i < details.size(); i++) {
                    ConcatRequestDetailDto detail = details.get(i);
                    MultipartFile file = vcService.findMultipartFileByName(files, detail.getLocalFileName());

                    if (file != null) {
                        LOGGER.info("매핑 중 - Detail localFileName: " + detail.getLocalFileName() + ", 파일명: " + file.getOriginalFilename());
                        detail.setSourceAudio(file);
                    }
                }
            }

            // 4. 서비스 로직 호출: 병합 처리 실행
            ConcatResponseDto concatResponse = concatService.convertAllConcatDetails(concatRequestDto, memberId);

            LOGGER.info("오디오 병합 성공"); // 성공 로그
            // 5. 성공적인 응답 반환
            return DataResponseDto.of(concatResponse);

        } catch (Exception e) {
            // 예외 발생 시 에러 로그 기록 및 커스텀 예외 반환
            log.error("오디오 병합 중 오류 발생", e);
            throw new BusinessException(ErrorCode.NOT_EXISTS_AUDIO); // 에러 코드 반환
        }
    }
}