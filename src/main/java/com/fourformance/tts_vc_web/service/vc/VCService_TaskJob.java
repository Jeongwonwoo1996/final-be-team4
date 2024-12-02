package com.fourformance.tts_vc_web.service.vc;


import com.fourformance.tts_vc_web.common.constant.APIStatusConst;
import com.fourformance.tts_vc_web.common.constant.APIUnitStatusConst;
import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.common.util.CommonFileUtils;
import com.fourformance.tts_vc_web.common.util.ElevenLabsClient_team_api;
import com.fourformance.tts_vc_web.domain.entity.*;
import com.fourformance.tts_vc_web.dto.common.VCMsgDto;
import com.fourformance.tts_vc_web.dto.vc.TrgAudioFileRequestDto;
import com.fourformance.tts_vc_web.dto.vc.VCDetailDto;
import com.fourformance.tts_vc_web.dto.vc.VCDetailResDto;
import com.fourformance.tts_vc_web.dto.vc.VCSaveRequestDto;
import com.fourformance.tts_vc_web.repository.*;
import com.fourformance.tts_vc_web.service.common.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VCService_TaskJob {

    private static final Logger LOGGER = Logger.getLogger(VCService_TaskJob.class.getName());

    // 의존성 주입
    private final ElevenLabsClient_team_api elevenLabsClient; // 외부 TTS 클라이언트
    private final S3Service s3Service; // S3 업로드 서비스
    private final MemberRepository memberRepository; // 멤버 리포지토리
    private final VCProjectRepository vcProjectRepository; // VC 프로젝트 리포지토리
    private final VCDetailRepository vcDetailRepository; // VC 디테일 리포지토리
    private final MemberAudioMetaRepository memberAudioMetaRepository; // 멤버 오디오 메타 리포지토리
    private final OutputAudioMetaRepository outputAudioMetaRepository; // 출력 오디오 메타 리포지토리
    private final VCService_team_multi vcService; // VC 프로젝트 저장 및 처리 서비스
    private final APIStatusRepository apiStatusRepository; // API 상태 리포지토리

    /**
     * VC 프로젝트 처리 메서드
     * 1. 멤버 검증
     * 2. VC 프로젝트 저장 및 ID 반환
     * 3. VC 디테일 정보 조회 및 처리
     * 4. 프로젝트 상태 업데이트
     */








    /**
     * 소스 파일 처리 및 변환 - 승민 변경
     */
    public VCDetailResDto processSourceFile( VCMsgDto vcMsgDto) {

        // 매칭된 파일 변환
        if (vcMsgDto.getMemberAudioId() != null) {
            // API 상태 기록 생성
            String requestPayload = String.format("Voice ID: %s, Source File: %s", vcMsgDto.getTrgVoiceId(), vcMsgDto.getLocalFileName());
            VCDetail vcDetail = vcDetailRepository.findById(vcMsgDto.getDetailId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.VC_DETAIL_NOT_FOUND));
            APIStatus apiStatus = APIStatus.createAPIStatus(vcDetail, null, requestPayload);
            apiStatusRepository.save(apiStatus);

            try {
                // 변환 작업 수행
                VCDetailResDto result = processSingleSourceFile(vcMsgDto);

                // 성공 상태 업데이트
                String responsePayload = String.format("변환 성공. 출력 URL: %s", result.getGenAudios());
                apiStatus.updateResponseInfo(responsePayload, 200, APIUnitStatusConst.SUCCESS);
                apiStatusRepository.save(apiStatus);

                return result;
            } catch (Exception e) {
                // 실패 상태 업데이트
                String responsePayload = String.format("변환 실패: %s", e.getMessage());
                apiStatus.updateResponseInfo(responsePayload, 500, APIUnitStatusConst.FAILURE);
                apiStatusRepository.save(apiStatus);
                return null;
            }
        }

        return null;
    }

    private VCDetailResDto processSingleSourceFile(VCMsgDto vcMsgDto) {

        String convertedFilePath = null;
        File convertedFile = null;
        try {
            // Step 1: 소스 파일 URL 가져오기
            String sourceFileUrl = memberAudioMetaRepository.findAudioUrlsByAudioMetaIds(
                    vcMsgDto.getMemberAudioId(),
                    AudioType.VC_SRC
            );
            LOGGER.info("[소스 파일 URL 조회] URL: " + sourceFileUrl);

            // Step 2: 변환 작업 수행
            convertedFilePath = elevenLabsClient.convertSpeechToSpeech(vcMsgDto.getTrgVoiceId(), sourceFileUrl);
            LOGGER.info("[파일 변환 완료] 파일 경로: " + convertedFilePath);

            // Step 3: 변환된 파일 읽기 및 S3 저장
            convertedFile = new File(convertedFilePath);
            byte[] convertedFileBytes = Files.readAllBytes(convertedFile.toPath());

            MultipartFile convertedMultipartFile = CommonFileUtils.convertFileToMultipartFile(convertedFile, convertedFile.getName());
            String vcOutputUrl = s3Service.uploadUnitSaveFile(convertedMultipartFile, vcMsgDto.getMemberId(), vcMsgDto.getProjectId(), vcMsgDto.getDetailId());
            LOGGER.info("[S3 업로드 완료] URL: " + vcOutputUrl);

            // Step 4: 결과 DTO 생성 및 반환
            return new VCDetailResDto(
                    vcMsgDto.getDetailId(),
                    vcMsgDto.getProjectId(),
                    true,
                    vcMsgDto.getUnitScript(),
                    sourceFileUrl,
                    List.of(vcOutputUrl)
            );
        } catch (Exception e) {
            LOGGER.severe("[소스 파일 변환 실패] " + e.getMessage());
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        } finally {
            // 변환 파일 삭제 로직을 finally 블록에 추가하여 항상 실행되도록 함
            if (convertedFile != null && convertedFile.exists()) {
                if (!convertedFile.delete()) {
                    LOGGER.warning("변환 파일 삭제 실패: " + convertedFile.getAbsolutePath());
                } else {
                    LOGGER.info("변환 파일 삭제 성공: " + convertedFilePath);
                }
            }
        }
    }



    /**
     * VC 프로젝트 상태 업데이트
     */
    public void updateProjectStatus(Long projectId) {
        List<VCDetail> details = vcDetailRepository.findByVcProject_Id(projectId);
        if (details.isEmpty()) {
            throw new BusinessException(ErrorCode.VC_DETAIL_NOT_FOUND);
        }
        boolean hasFailure = false;
        boolean allSuccess = true;
        for (VCDetail detail : details) {
            List<APIStatus> apiStatuses = detail.getApiStatuses();
            if (apiStatuses.stream().anyMatch(status -> status.getApiUnitStatusConst() == APIUnitStatusConst.FAILURE)) {
                hasFailure = true;
                allSuccess = false;
                break;
            }
            if (!apiStatuses.stream().allMatch(status -> status.getApiUnitStatusConst() == APIUnitStatusConst.SUCCESS)) {
                allSuccess = false;
            }
        }
        VCProject project = vcProjectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));
        if (hasFailure) {
            project.updateAPIStatus(APIStatusConst.FAILURE);
        } else if (allSuccess) {
            project.updateAPIStatus(APIStatusConst.SUCCESS);
        } else {
            project.updateAPIStatus(APIStatusConst.NOT_STARTED);
        }
        vcProjectRepository.save(project);
        LOGGER.info("[VC 프로젝트 상태 업데이트 완료]");
    }

}

