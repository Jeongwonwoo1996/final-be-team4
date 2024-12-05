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

    // 파일 업로드 디렉토리
    @Value("${upload.dir}")
    private String uploadDir;

    /**
     * VC 프로젝트 처리 메인 함수
     * - 프로젝트 생성 및 소스/타겟 파일 처리
     */
    public List<VCDetailResDto> processVCProject(VCSaveRequestDto vcSaveRequestDto, List<MultipartFile> files, Long memberId) {
        LOGGER.info("[VC 프로젝트 시작]");

        // Step 1: 멤버 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // Step 2: 프로젝트 저장
        Long projectId = vcService.saveVCProject(vcSaveRequestDto, files, member);
        if (projectId == null) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }
        LOGGER.info("[VC 프로젝트 저장 완료] 프로젝트 ID: " + projectId);

        // Step 3: VC 디테일 가져오기
        List<VCDetail> vcDetails = vcDetailRepository.findByVcProject_Id(projectId);
        List<VCDetailDto> vcDetailDtos = vcDetails.stream()
                .filter(vcDetail -> vcDetail.getIsChecked() && !vcDetail.getIsDeleted())
                .map(VCDetailDto::createVCDetailDtoWithLocalFileName)
                .collect(Collectors.toList());

        // Step 4: 타겟 파일 처리
        String voiceId = processTargetFiles(vcSaveRequestDto.getTrgFiles(), memberId);

        // Step 5: 소스 파일 처리 (로컬 + S3)
        List<MultipartFile> combinedFiles = combineLocalAndS3Files(vcSaveRequestDto, files);
        List<VCDetailResDto> vcDetailsRes = processSourceFiles(combinedFiles, vcDetailDtos, voiceId, memberId);

        // Step 6: 상태 업데이트
        updateProjectStatus(projectId);
        return vcDetailsRes;
    }




    /**
     * 타겟 파일 처리
     * - Voice ID 생성 및 반환
     */
    private String processTargetFiles(List<TrgAudioFileRequestDto> trgFiles, Long memberId) {
        MemberAudioMeta memberAudio = memberAudioMetaRepository.findSelectedAudioByTypeAndMember(AudioType.VC_TRG, memberId);
        if (memberAudio != null) {
            return "DNSy71aycodz7FWtd91e"; // 하드코딩 Voice ID
        } else {
            if (trgFiles == null || trgFiles.isEmpty()) {
                throw new BusinessException(ErrorCode.TRG_FILES_EMPTY);
            }
            String voiceId = memberAudioMetaRepository.findtrgVoiceIdById(trgFiles.get(0).getS3MemberAudioMetaId());
            if (voiceId == null) {
                throw new BusinessException(ErrorCode.INVALID_VOICE_ID);
            }
            return voiceId;
        }
    }

    /**
     * 로컬 및 S3 파일 결합
     * - 로컬에서 업로드된 파일과 S3에서 다운로드한 파일 결합
     */
    private List<MultipartFile> combineLocalAndS3Files(VCSaveRequestDto vcSaveRequestDto, List<MultipartFile> localFiles) {
        List<MultipartFile> combinedFiles = new ArrayList<>();

        // 로컬 파일 추가
        if (localFiles != null && !localFiles.isEmpty()) {
            combinedFiles.addAll(localFiles);
        }

        // S3 파일 다운로드
        List<String> s3Urls = vcSaveRequestDto.getSrcFiles().stream()
                .filter(srcFile -> srcFile.getMemberAudioMetaId() != null)
                .map(srcFile -> memberAudioMetaRepository.findById(srcFile.getMemberAudioMetaId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_AUDIO_META_NOT_FOUND))
                        .getAudioUrl())
                .collect(Collectors.toList());
        combinedFiles.addAll(downloadS3FilesAsMultipart(s3Urls));

        return combinedFiles;
    }

    /**
     * S3에서 소스 파일 다운로드 및 MultipartFile 변환
     */
    private List<MultipartFile> downloadS3FilesAsMultipart(List<String> s3Urls) {
        List<MultipartFile> files = new ArrayList<>();
        for (String url : s3Urls) {
            try {
                String localPath = s3Service.downloadFileFromS3(url, uploadDir);
                File file = new File(localPath);
                MultipartFile multipartFile = CommonFileUtils.convertFileToMultipartFile(file, file.getName());
                files.add(multipartFile);
            } catch (Exception e) {
                LOGGER.severe("S3 파일 다운로드 실패: " + url + ", 이유: " + e.getMessage());
                throw new BusinessException(ErrorCode.S3_DOWNLOAD_FAILED);
            }
        }
        return files;
    }

    /**
     * 소스 파일 처리
     * - 소스 파일과 다운로드된 파일을 병합하여 처리
     */
    private List<VCDetailResDto> processSourceFiles(
            List<MultipartFile> inputFiles,
            List<VCDetailDto> srcFiles,
            String voiceId,
            Long memberId) {

        List<String> downloadedFiles = downloadSrcFilesFromS3(srcFiles);
        List<MultipartFile> convertedFiles = convertLocalFilesToMultipart(downloadedFiles);

        List<MultipartFile> files = inputFiles != null ? inputFiles : new ArrayList<>();
        files.addAll(convertedFiles);

        return srcFiles.stream()
                .map(srcFile -> {
                    MultipartFile matchingFile = findMatchingFile(files, srcFile);
                    LOGGER.info("[소스 파일 매칭] 파일명: " + (matchingFile != null ? matchingFile.getOriginalFilename() : "null"));

                    if (matchingFile != null) {
                        return processSingleSourceFile(srcFile, matchingFile, voiceId, memberId);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    /**
     * 단일 소스 파일 처리
     * - 파일을 변환하고 S3에 업로드
     */
    private VCDetailResDto processSingleSourceFile(VCDetailDto srcFile, MultipartFile matchingFile, String voiceId, Long memberId) {
        try {
            String convertedFilePath = elevenLabsClient.convertSpeechToSpeech(voiceId, matchingFile.getOriginalFilename());
            File convertedFile = new File(convertedFilePath);

            MultipartFile convertedMultipartFile = CommonFileUtils.convertFileToMultipartFile(convertedFile, convertedFile.getName());
            String uploadedUrl = s3Service.uploadUnitSaveFile(convertedMultipartFile, memberId, srcFile.getProjectId(), srcFile.getId());


            return new VCDetailResDto(
                    srcFile.getId(),
                    srcFile.getProjectId(),
                    srcFile.getIsChecked(),
                    srcFile.getUnitScript(),
                    matchingFile.getOriginalFilename(),
                    List.of(uploadedUrl)
            );
        } catch (Exception e) {
            LOGGER.severe("소스 파일 처리 실패: " + e.getMessage());
            throw new BusinessException(ErrorCode.SERVER_ERROR);

        }
    }

    /**
     * 매칭 파일 찾기
     */
    private MultipartFile findMatchingFile(List<MultipartFile> files, VCDetailDto srcFile) {
        if (srcFile.getMemberAudioMetaId() != null) {
            LOGGER.info("[매칭 시작] memberAudioId: " + srcFile.getMemberAudioMetaId());

            return files.stream()
                    .filter(file -> file.getOriginalFilename().contains("audio" + srcFile.getMemberAudioMetaId()))
                    .findFirst()
                    .orElse(null);
        }

        if (srcFile.getLocalFileName() != null) {
            String simpleFileName = srcFile.getLocalFileName().substring(srcFile.getLocalFileName().lastIndexOf("/") + 1);
            LOGGER.info("[매칭 시작] localFileName: " + simpleFileName);

            return files.stream()
                    .filter(file -> file.getOriginalFilename().equals(simpleFileName))
                    .findFirst()
                    .orElse(null);
        }

        LOGGER.warning("[매칭 실패] localFileName과 memberAudioId가 모두 null입니다. srcFile ID: " + srcFile.getId());
        return null;
    }

    /**
     * S3에서 VC 소스 파일 다운로드
     */
    private List<String> downloadSrcFilesFromS3(List<VCDetailDto> srcFiles) {
        List<String> downloadedFilePaths = new ArrayList<>();
        for (VCDetailDto srcFile : srcFiles) {
            if (srcFile.getId() != null) {
                VCDetail vcDetail = vcDetailRepository.findById(srcFile.getId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.VC_DETAIL_NOT_FOUND));

                String fileUrl = vcDetail.getMemberAudioMeta().getAudioUrl();
                if (fileUrl != null) {
                    String downloadedFilePath = s3Service.downloadFileFromS3(fileUrl, uploadDir);
                    downloadedFilePaths.add(downloadedFilePath);
                    LOGGER.info("[파일 다운로드 성공] 파일 경로: " + downloadedFilePath);
                } else {
                    LOGGER.warning("[S3 URL 없음] 소스 파일 ID: " + srcFile.getId());
                }
            }
        }
        return downloadedFilePaths;
    }

    /**
     * 로컬 파일을 MultipartFile로 변환
     */
    private List<MultipartFile> convertLocalFilesToMultipart(List<String> localFilePaths) {
        return localFilePaths.stream()
                .map(localPath -> {
                    File file = new File(localPath);
                    try {
                        return CommonFileUtils.convertFileToMultipartFile(file, file.getName());
                    } catch (IOException e) {
                        LOGGER.warning("[로컬 파일 변환 실패] 파일: " + localPath + ", 에러: " + e.getMessage());
                        throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
                    }
                })
                .collect(Collectors.toList());
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
    }
}


