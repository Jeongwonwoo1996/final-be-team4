package com.fourformance.tts_vc_web.service.vc;

import com.fourformance.tts_vc_web.common.constant.APIStatusConst;
import com.fourformance.tts_vc_web.common.constant.APIUnitStatusConst;
import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.common.util.CommonFileUtils;
import com.fourformance.tts_vc_web.common.util.ElevenLabsClient_team_api;
import com.fourformance.tts_vc_web.domain.entity.*;
import com.fourformance.tts_vc_web.dto.vc.TrgAudioFileRequestDto;
import com.fourformance.tts_vc_web.dto.vc.VCDetailDto;
import com.fourformance.tts_vc_web.dto.vc.VCDetailResDto;
import com.fourformance.tts_vc_web.dto.vc.VCSaveRequestDto;
import com.fourformance.tts_vc_web.repository.*;
import com.fourformance.tts_vc_web.service.common.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VCService_team_api {

    private static final Logger LOGGER = Logger.getLogger(VCService_team_api.class.getName());

    // 의존성 주입
    private final ElevenLabsClient_team_api elevenLabsClient;
    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final VCProjectRepository vcProjectRepository;
    private final VCDetailRepository vcDetailRepository;
    private final MemberAudioMetaRepository memberAudioMetaRepository;
    private final OutputAudioMetaRepository outputAudioMetaRepository;
    private final VCService_team_multi vcService;
    private final APIStatusRepository apiStatusRepository;

    @Value("${upload.dir}")
    private String uploadDir;

    /**
     * VC 프로젝트 처리 메서드
     */
//    public List<VCDetailResDto> processVCProject(VCSaveRequestDto vcSaveRequestDto, List<MultipartFile> files, Long memberId) {
//        LOGGER.info("[VC 프로젝트 시작]");
//
//        // Step 1: 멤버 검증
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
//
//        // Step 2: VC 프로젝트 저장 및 ID 반환
//        Long projectId = vcService.saveVCProject(vcSaveRequestDto, files, member);
//        if (projectId == null) {
//            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
//        }
//        LOGGER.info("[VC 프로젝트 저장 완료] 프로젝트 ID: " + projectId);
//
//        // Step 3: VC 디테일 정보 조회 및 처리
//        List<VCDetailResDto> vcDetailsRes = processVCDetails(projectId, vcSaveRequestDto, files, memberId);
//
//        // Step 4: 프로젝트 상태 업데이트
//        updateProjectStatus(projectId);
//        LOGGER.info("[VC 프로젝트 상태 업데이트 완료] 프로젝트 ID: " + projectId);
//
//        return vcDetailsRes;
//    }
    public List<VCDetailResDto> processVCProject(VCSaveRequestDto vcSaveRequestDto, List<MultipartFile> files, Long memberId) {
        LOGGER.info("[VC 프로젝트 시작]");
        Map<String, MultipartFile> fileMap = createFileMap(files);

        vcSaveRequestDto.getSrcFiles().forEach(srcFile -> {
            String strippedLocalFileName = srcFile.getLocalFileName().substring(srcFile.getLocalFileName().lastIndexOf('/') + 1);
            MultipartFile sourceAudio = fileMap.get(strippedLocalFileName);

            if (sourceAudio == null) {
                LOGGER.warning("파일 매핑 실패: " + strippedLocalFileName);
            } else {
                srcFile.setSourceAudio(sourceAudio);
                LOGGER.info("매핑 성공: " + strippedLocalFileName + " -> " + sourceAudio.getOriginalFilename());
            }
        });

        LOGGER.info("[VC 프로젝트 완료]");
        return Collections.emptyList();
    }

    /**
     * VC 디테일 처리 메서드
     */
    private List<VCDetailResDto> processVCDetails(Long projectId, VCSaveRequestDto vcSaveRequestDto, List<MultipartFile> files, Long memberId) {
        // 프로젝트 ID로 연관된 VC 디테일 조회
        List<VCDetail> vcDetails = vcDetailRepository.findByVcProject_Id(projectId);
        LOGGER.info("[VC 디테일 조회 완료] 디테일 개수: " + vcDetails.size());

        // VC 디테일 DTO 변환 및 필터링 (체크된 항목만)
        List<VCDetailDto> vcDetailDtos = vcDetails.stream()
                .filter(vcDetail -> vcDetail.getIsChecked() && !vcDetail.getIsDeleted())
                .map(VCDetailDto::createVCDetailDtoWithLocalFileName)
                .collect(Collectors.toList());
        LOGGER.info("[VC 디테일 필터링 완료] 체크된 디테일 개수: " + vcDetailDtos.size());

        // 타겟 파일 처리 및 Voice ID 생성
        String voiceId = processTargetFiles(vcSaveRequestDto.getTrgFiles(), memberId);

        // 소스 파일 처리 및 변환
        List<VCDetailResDto> vcDetailsRes = processSourceFiles(vcDetailDtos, files, voiceId, memberId);

        return vcDetailsRes;
    }

    /**
     * 타겟 파일 처리 및 Voice ID 생성
     * 기존 하드코딩된 코드 유지
     */
    private String processTargetFiles(List<TrgAudioFileRequestDto> trgFiles, Long memberId) {
        // 하드코딩된 Voice ID 사용
        String voiceId = "DNSy71aycodz7FWtd91e"; // 기존 하드코딩된 코드 유지
        LOGGER.info("[Voice ID 하드코딩 적용] Voice ID: " + voiceId);
        return voiceId;
    }

    /**
     * 소스 파일 처리 및 변환
     */
    private List<VCDetailResDto> processSourceFiles(
            List<VCDetailDto> srcFiles,
            List<MultipartFile> files,
            String voiceId,
            Long memberId) {

        Map<String, MultipartFile> fileMap = files != null ? files.stream()
                .collect(Collectors.toMap(MultipartFile::getOriginalFilename, file -> file)) : new HashMap<>();

        return srcFiles.stream()
                .map(srcFile -> {

                    // 디버깅 코드 추가
                    LOGGER.info("[소스 파일 확인] srcFile ID: " + srcFile.getId() +
                            ", localFileName: " + srcFile.getLocalFileName() +
                            ", memberAudioMetaId: " + srcFile.getMemberAudioMetaId());

                    try {
                        MultipartFile sourceAudio = null;
                        String sourceFileUrl = null;

                        if (srcFile.getLocalFileName() != null) {
                            sourceAudio = fileMap.get(srcFile.getLocalFileName());
                            System.out.println("sourceAudio = " + sourceAudio);
                            if (sourceAudio == null) {
                                LOGGER.severe("[로컬 파일 누락] 파일명: " + srcFile.getLocalFileName());
                                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
                            }
                        } else if (srcFile.getMemberAudioMetaId() != null) {
                            sourceFileUrl = memberAudioMetaRepository.findAudioUrlsByAudioMetaIds(
                                    srcFile.getMemberAudioMetaId(),
                                    AudioType.VC_SRC
                            );
                            if (sourceFileUrl == null) {
                                LOGGER.severe("[S3 파일 누락] memberAudioMetaId: " + srcFile.getMemberAudioMetaId());
                                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
                            }
                        } else {
                            LOGGER.severe("[소스 파일 누락] localFileName과 memberAudioMetaId가 모두 null");
                            throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA);
                        }

                        return processSingleSourceFile(srcFile, sourceAudio, sourceFileUrl, voiceId, memberId);
                    } catch (Exception e) {
                        LOGGER.severe("[소스 파일 처리 실패] srcFile ID: " + srcFile.getId() + ", 이유: " + e.getMessage());
                        throw new BusinessException(ErrorCode.SERVER_ERROR);
                    }
                })
                .collect(Collectors.toList());
    }


    /**
     * 단일 소스 파일 변환 처리
     */
    private VCDetailResDto processSingleSourceFile(
            VCDetailDto srcFile,
            MultipartFile sourceAudio,
            String sourceFileUrl,
            String voiceId,
            Long memberId) {

        File tempFile = null;
        File convertedFile = null;

        try {
            String inputFilePath;

            if (sourceAudio != null) {
                // 로컬 파일인 경우, 임시 파일로 저장
                String tempFilePath = uploadDir + File.separator + sourceAudio.getOriginalFilename();
                tempFile = new File(tempFilePath);
                sourceAudio.transferTo(tempFile);
                inputFilePath = tempFilePath;
                LOGGER.info("로컬 소스 오디오 저장 완료: " + inputFilePath);
            } else if (sourceFileUrl != null) {
                // S3 파일인 경우, 다운로드하여 임시 파일로 저장
                inputFilePath = s3Service.downloadFileFromS3(sourceFileUrl, uploadDir);
                LOGGER.info("S3 소스 오디오 다운로드 완료: " + inputFilePath);
            } else {
                LOGGER.warning("소스 오디오를 찾을 수 없습니다.");
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
            }

            // 음성 변환 수행
            String convertedFilePath = elevenLabsClient.convertSpeechToSpeech(voiceId, inputFilePath);

            // 변환된 파일을 MultipartFile로 변환
            convertedFile = new File(convertedFilePath);
            MultipartFile convertedMultipartFile = CommonFileUtils.convertFileToMultipartFile(
                    convertedFile, convertedFile.getName());

            // 변환된 파일을 S3에 업로드
            String uploadedUrl = s3Service.uploadUnitSaveFile(
                    convertedMultipartFile, memberId, srcFile.getProjectId(), srcFile.getId());

            // 결과 DTO 생성
            return new VCDetailResDto(
                    srcFile.getId(),
                    srcFile.getProjectId(),
                    srcFile.getIsChecked(),
                    srcFile.getUnitScript(),
                    sourceAudio != null ? sourceAudio.getOriginalFilename() : sourceFileUrl,
                    List.of(uploadedUrl)
            );
        } catch (Exception e) {
            LOGGER.severe("소스 파일 처리 실패: " + e.getMessage());
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        } finally {
            // 임시 파일 정리
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
                LOGGER.info("임시 소스 파일 삭제: " + tempFile.getAbsolutePath());
            }
            if (convertedFile != null && convertedFile.exists()) {
                convertedFile.delete();
                LOGGER.info("변환된 파일 삭제: " + convertedFile.getAbsolutePath());
            }
        }
    }


    /**
     * MultipartFile을 임시 디렉토리에 저장하고 File 객체 반환
     */
    private File saveMultipartFileToTemp(MultipartFile file) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File tempFile = new File(tempDir, file.getOriginalFilename());
        file.transferTo(tempFile);
        return tempFile;
    }

    /**
     * 프로젝트 상태 업데이트
     */
    private void updateProjectStatus(Long projectId) {
        List<VCDetail> details = vcDetailRepository.findByVcProject_Id(projectId);
        if (details.isEmpty()) {
            throw new BusinessException(ErrorCode.VC_DETAIL_NOT_FOUND);
        }
        boolean hasFailure = details.stream()
                .flatMap(detail -> detail.getApiStatuses().stream())
                .anyMatch(status -> status.getApiUnitStatusConst() == APIUnitStatusConst.FAILURE);
        boolean allSuccess = details.stream()
                .flatMap(detail -> detail.getApiStatuses().stream())
                .allMatch(status -> status.getApiUnitStatusConst() == APIUnitStatusConst.SUCCESS);

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

    private Map<String, MultipartFile> createFileMap(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            LOGGER.info("[업로드된 파일이 없습니다.]");
            return Map.of();
        }
        return files.stream().collect(Collectors.toMap(MultipartFile::getOriginalFilename, file -> file));
    }
}
