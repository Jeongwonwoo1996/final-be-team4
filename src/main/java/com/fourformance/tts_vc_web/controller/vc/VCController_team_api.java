package com.fourformance.tts_vc_web.controller.vc;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.dto.vc.VCDetailResDto;
import com.fourformance.tts_vc_web.dto.vc.VCSaveRequestDto;
import com.fourformance.tts_vc_web.service.vc.VCService_team_api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
     * @param vcSaveRequestDto 프로젝트 저장 및 처리 요청 데이터
     * @param files            소스 오디오 파일 리스트
     * @param session          현재 HTTP 세션 (회원 정보 저장)
     * @return VCDetailResDto의 리스트
     */
    @Operation(summary = "VC 프로젝트 처리", description = "소스/타겟 오디오 파일 처리 및 Voice ID 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "VC 프로젝트 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping(value = "/process", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto processVCProject(
            @RequestPart("VCSaveRequestDto") VCSaveRequestDto vcSaveRequestDto,
            @RequestPart(value = "files", required = false) @Parameter(description = "업로드할 파일들") List<MultipartFile> files,
            HttpSession session) {

        LOGGER.info("[VC 프로젝트 처리 요청 시작]");

        // Step 1: 세션에서 memberId 검증 및 추출
        Long memberId = validateAndExtractMemberId(session);

        // Step 2: 요청 데이터 유효성 검사
        validateRequestData(vcSaveRequestDto);

        try {
            // Step 3: 파일 매핑 (files와 요청 데이터 간 연결)
            Map<String, MultipartFile> fileMap = createFileMap(files);
            mapFilesToSrcFiles(vcSaveRequestDto, fileMap);

            // Step 4: 서비스 호출하여 VC 프로젝트 처리
            List<VCDetailResDto> response = vcService.processVCProject(vcSaveRequestDto, files, memberId);

            LOGGER.info("[VC 프로젝트 처리 성공]");
            return DataResponseDto.of(response);

        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "[비즈니스 예외 발생]", e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[시스템 예외 발생]", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_VC_ERROR);
        }
    }

    /**
     * 세션에서 memberId를 검증 및 추출
     */
    private Long validateAndExtractMemberId(HttpSession session) {
        Object memberIdObj = session.getAttribute("memberId");
        if (memberIdObj == null) {
            throw new BusinessException(ErrorCode.SESSION_MEMBER_ID_NOT_SET);
        }
        try {
            return Long.parseLong(memberIdObj.toString());
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.INVALID_MEMBER_ID);
        }
    }

    /**
     * 요청 데이터 유효성 검사
     */
//    private void validateRequestData(VCSaveRequestDto vcSaveRequestDto) {
//        if (vcSaveRequestDto == null) {
//            LOGGER.warning("[요청 데이터가 null입니다.]");
//            throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA);
//        }
//        if (vcSaveRequestDto.getTrgFiles() == null || vcSaveRequestDto.getTrgFiles().isEmpty()) {
//            LOGGER.warning("[타겟 파일이 없습니다.]");
//            throw new BusinessException(ErrorCode.INVALID_REQUEST_FILE_DATA);
//        }
//        if (vcSaveRequestDto.getSrcFiles() == null || vcSaveRequestDto.getSrcFiles().isEmpty()) {
//            LOGGER.warning("[소스 파일이 없습니다.]");
//            throw new BusinessException(ErrorCode.INVALID_REQUEST_FILE_DATA);
//        }
//        LOGGER.info("[요청 데이터 유효성 검사 통과]");
//    }
    private void validateRequestData(VCSaveRequestDto vcSaveRequestDto) {
        if (vcSaveRequestDto == null || vcSaveRequestDto.getTrgFiles() == null || vcSaveRequestDto.getTrgFiles().isEmpty()
                || vcSaveRequestDto.getSrcFiles() == null || vcSaveRequestDto.getSrcFiles().isEmpty()) {
            LOGGER.warning("[요청 데이터가 잘못되었습니다.]");
            throw new BusinessException(ErrorCode.INVALID_REQUEST_FILE_DATA);
        }
        LOGGER.info("[요청 데이터 유효성 검사 통과]");
    }

    /**
     * 업로드된 파일을 맵으로 변환 (파일명 기준)
     */
//    private Map<String, MultipartFile> createFileMap(List<MultipartFile> files) {
//        if (files == null || files.isEmpty()) {
//            LOGGER.info("[업로드된 파일이 없습니다.]");
//            return Map.of();
//        }
//        return files.stream()
//                .collect(Collectors.toMap(file -> {
//                    String filename = file.getOriginalFilename();
//                    return filename != null ? filename.substring(filename.lastIndexOf('/') + 1) : filename;
//                }, file -> file));
//    }
    private Map<String, MultipartFile> createFileMap(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            LOGGER.info("[업로드된 파일이 없습니다.]");
            return Map.of();
        }
        Map<String, MultipartFile> fileMap = files.stream()
                .collect(Collectors.toMap(file -> {
                    String filename = file.getOriginalFilename();
                    return filename != null ? filename.substring(filename.lastIndexOf('/') + 1) : filename;
                }, file -> file));
        LOGGER.info("[생성된 fileMap 키 확인] " + fileMap.keySet());
        return fileMap;
    }

    /**
     * 요청 데이터의 srcFiles와 업로드된 파일을 매핑
     */
    private void mapFilesToSrcFiles(VCSaveRequestDto vcSaveRequestDto, Map<String, MultipartFile> fileMap) {
        vcSaveRequestDto.getSrcFiles().forEach(srcFile -> {
            if (srcFile.getLocalFileName() != null) {
                String strippedLocalFileName = srcFile.getLocalFileName().substring(srcFile.getLocalFileName().lastIndexOf('/') + 1);
                MultipartFile file = fileMap.get(strippedLocalFileName);
                if (file != null) {
                    srcFile.setSourceAudio(file);
                    LOGGER.info("매핑 성공: " + strippedLocalFileName + " -> " + file.getOriginalFilename());
                } else {
                    LOGGER.warning("파일 매핑 실패: " + strippedLocalFileName);
                }
            } else {
                LOGGER.info("S3 기반 파일로 매핑됩니다: " + srcFile.getMemberAudioMetaId());
            }
        });
    }
}