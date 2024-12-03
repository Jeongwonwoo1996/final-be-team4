package com.fourformance.tts_vc_web.controller.concat;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.dto.concat.ConcatRequestDetailDto;
import com.fourformance.tts_vc_web.dto.concat.ConcatRequestDto;
import com.fourformance.tts_vc_web.dto.concat.ConcatResponseDto;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.service.concat.ConcatService_TaskJob;
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
    private final ConcatService_TaskJob concatTaskService; // 병합 서비스 의존성 주입
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
            @RequestPart("files") @Parameter(description = "업로드할 파일들") List<MultipartFile> files, HttpSession session
    ) {
        LOGGER.info("컨트롤러 메서드 호출됨: " + concatRequestDto); // 요청 데이터 로깅

        // 세션에 memberId 값이 설정되지 않았다면 예외 처리
        if (session.getAttribute("memberId") == null) {
            throw new BusinessException(ErrorCode.SESSION_MEMBER_ID_NOT_SET);
        }

        // 세션에 memberId 값 설정
        Long memberId = (Long) session.getAttribute("memberId");


        concatTaskService.enqueueConcatTask(concatRequestDto,files,memberId);


        return DataResponseDto.of("Concat 작업이 큐에 추가되었습니다.");
    }
}
