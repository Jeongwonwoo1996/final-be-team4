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
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * VC 프로젝트를 처리하는 서비스 클래스입니다.
 * 음원 파일을 로컬 또는 S3에서 처리하여 변환 후 저장하는 기능을 담당합니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VCService_team_api {

    // 의존성 주입을 위한 필드들
    private final ElevenLabsClient_team_api elevenLabsClient; // 음성 변환 클라이언트
    private final S3Service s3Service; // S3 관련 서비스
    private final MemberRepository memberRepository; // 회원 정보 저장소
    private final VCProjectRepository vcProjectRepository; // VC 프로젝트 저장소
    private final VCDetailRepository vcDetailRepository; // VC 디테일 저장소
    private final MemberAudioMetaRepository memberAudioMetaRepository; // 회원 오디오 메타 저장소
    private final OutputAudioMetaRepository outputAudioMetaRepository; // 출력 오디오 메타 저장소
    private final VCService_team_multi vcService; // 다중 VC 서비스 (추가 기능 담당)
    private final APIStatusRepository apiStatusRepository; // API 상태 저장소

    // 업로드 디렉토리 경로를 설정 파일에서 주입받음
    @Value("${upload.dir}")
    private String uploadDir;

    /**
     * VC 프로젝트를 처리하는 메서드입니다.
     * 주어진 요청 DTO와 업로드된 파일들을 바탕으로 프로젝트를 생성하고, 관련 디테일을 처리합니다.
     *
     * @param vcSaveRequestDto VC 프로젝트 저장 요청 DTO
     * @param files            업로드된 파일 리스트
     * @param memberId         회원 ID
     * @return 처리된 VC 디테일 응답 DTO 리스트
     */
    public List<VCDetailResDto> processVCProject(VCSaveRequestDto vcSaveRequestDto, List<MultipartFile> files, Long memberId) {
        log.info("[VC 프로젝트 시작] memberId: {}", memberId);

        // 파일 매핑
        Map<String, MultipartFile> fileMap = createFileMap(files);
        log.debug("파일 매핑 완료: {}", fileMap.keySet());

        // 요청 DTO에 포함된 소스 파일들에 대해 로컬 파일과 매핑 시도
        vcSaveRequestDto.getSrcFiles().forEach(srcFile -> {
            // localFileName에서 실제 파일 이름만 추출
            String strippedLocalFileName = srcFile.getLocalFileName() != null
                    ? Paths.get(srcFile.getLocalFileName()).getFileName().toString()
                    : null;
            if (strippedLocalFileName != null) {
                MultipartFile sourceAudio = fileMap.get(strippedLocalFileName);
                if (sourceAudio != null) {
                    // 매핑 성공: DTO에 실제 MultipartFile 설정
                    srcFile.setSourceAudio(sourceAudio);
                    log.info("매핑 성공: {} -> {}", strippedLocalFileName, sourceAudio.getOriginalFilename());
                } else {
                    // 매핑 실패: 로컬 파일이 업로드되지 않았거나 이름이 일치하지 않음
                    log.warn("파일 매핑 실패: {}", strippedLocalFileName);
                }
            } else {
                log.debug("소스 파일에 localFileName이 설정되어 있지 않음: srcFileId={}", srcFile.getId());
            }
        });

        // Step 1: 회원 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("회원 조회 실패: memberId={}", memberId);
                    return new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
                });
        log.info("회원 조회 성공: memberId={}, memberName={}", memberId, member.getName());

        // Step 2: VC 프로젝트 저장 및 프로젝트 ID 반환
        Long projectId = vcService.saveVCProject(vcSaveRequestDto, files, member);
        if (projectId == null) {
            log.error("VC 프로젝트 저장 실패: 프로젝트가 반환되지 않음");
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }
        log.info("[VC 프로젝트 저장 완료] 프로젝트 ID: {}", projectId);

        // Step 3: VC 디테일 정보 조회 및 처리
        List<VCDetailResDto> vcDetailsRes = processVCDetails(projectId, vcSaveRequestDto, files, memberId);
        log.info("[VC 디테일 처리 완료] 처리된 VC 디테일 개수: {}", vcDetailsRes.size());

        // Step 4: 프로젝트 상태 업데이트
        updateProjectStatus(projectId);
        log.info("[VC 프로젝트 상태 업데이트 완료] 프로젝트 ID: {}", projectId);

        return vcDetailsRes;
    }

    /**
     * VC 디테일 정보를 조회하고 처리하는 메서드입니다.
     *
     * @param projectId        프로젝트 ID
     * @param vcSaveRequestDto VC 프로젝트 저장 요청 DTO
     * @param files            업로드된 파일 리스트
     * @param memberId         회원 ID
     * @return 처리된 VC 디테일 응답 DTO 리스트
     */
    private List<VCDetailResDto> processVCDetails(Long projectId, VCSaveRequestDto vcSaveRequestDto, List<MultipartFile> files, Long memberId) {
        // 프로젝트 ID로 관련된 VC 디테일 조회
        List<VCDetail> vcDetails = vcDetailRepository.findByVcProject_Id(projectId);
        log.info("[VC 디테일 조회 완료] 프로젝트 ID: {}, 디테일 개수: {}", projectId, vcDetails.size());

        // VC 디테일을 DTO로 변환하고, 체크된 항목만 필터링
        List<VCDetailDto> vcDetailDtos = vcDetails.stream()
                .filter(vcDetail -> vcDetail.getIsChecked() && !vcDetail.getIsDeleted())
                .map(VCDetailDto::createVCDetailDtoWithLocalFileName)
                .collect(Collectors.toList());
        log.info("[VC 디테일 필터링 완료] 체크된 디테일 개수: {}", vcDetailDtos.size());

        // 타겟 파일 처리 및 Voice ID 생성 (현재는 하드코딩)
        String voiceId = processTargetFiles(vcSaveRequestDto.getTrgFiles(), memberId);
        log.debug("Voice ID 생성 완료: {}", voiceId);

        // 소스 파일 처리 및 변환
        List<VCDetailResDto> vcDetailsRes = processSourceFiles(vcDetailDtos, files, voiceId, memberId);
        log.info("[소스 파일 처리 및 변환 완료] 변환된 VC 디테일 개수: {}", vcDetailsRes.size());

        return vcDetailsRes;
    }

    //    /**
//     * 타겟 파일을 처리하고 Voice ID를 생성하는 메서드입니다.
//     *
//     * @param trgFiles 타겟 오디오 파일 요청 DTO 리스트
//     * @param memberId 회원 ID
//     * @return 생성된 Voice ID
//     */
//    private String processTargetFiles(List<TrgAudioFileRequestDto> trgFiles, Long memberId) {
//        if (trgFiles == null || trgFiles.isEmpty()) {
//            log.error("[타겟 파일 처리 실패] 타겟 파일이 비어있습니다.");
//            throw new BusinessException(ErrorCode.TRG_FILES_EMPTY);
//        }
//
//        try {
//            // Step 1: MemberAudioMeta 조회
//            MemberAudioMeta memberAudio = memberAudioMetaRepository.findSelectedAudioByTypeAndMember(AudioType.VC_TRG, memberId);
//            if (memberAudio == null || memberAudio.getAudioUrl() == null) {
//                log.error("[타겟 오디오 처리 실패] MemberAudioMeta가 존재하지 않거나 URL이 없습니다.");
//                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
//            }
//
//            String targetFileUrl = memberAudio.getAudioUrl();
//            log.info("[타겟 오디오 업로드 시작] URL: {}", targetFileUrl);
//
//            // Step 2: Voice ID 생성
//            String voiceId = elevenLabsClient.uploadVoice(targetFileUrl);
//            log.info("[Voice ID 생성 완료] Voice ID: {}", voiceId);
//
//            // Step 3: Voice ID 저장
//            memberAudio.update(voiceId);
//            memberAudioMetaRepository.save(memberAudio);
//            log.info("[MemberAudioMeta 업데이트 완료] Voice ID: {}", voiceId);
//
//            return voiceId;
//
//        } catch (IOException e) {
//            log.error("[Voice ID 생성 실패] {}", e.getMessage(), e);
//            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
//        }
//    }

    /**
     * 타겟 파일을 처리하고 Voice ID를 생성하는 메서드입니다.
     * 현재는 Voice ID가 하드코딩되어 있습니다.
     *
     * @param trgFiles 타겟 오디오 파일 요청 DTO 리스트
     * @param memberId 회원 ID
     * @return 생성된 Voice ID
     */
    private String processTargetFiles(List<TrgAudioFileRequestDto> trgFiles, Long memberId) {
        // 하드코딩된 Voice ID 사용 (추후 동적 생성 필요)
        String voiceId = "0lOWjOLAk4FS2t9EgVFd";
        log.info("[Voice ID 하드코딩 적용] Voice ID: {}", voiceId);
        return voiceId;
    }

    /**
     * 소스 파일들을 처리하고 변환하는 메서드입니다.
     * 각 소스 파일에 대해 S3에서 파일을 가져오거나 로컬 파일을 사용하여 음성을 변환합니다.
     *
     * @param srcFiles  소스 파일 요청 DTO 리스트
     * @param files     업로드된 파일 리스트
     * @param voiceId   Voice ID
     * @param memberId  회원 ID
     * @return 변환된 VC 디테일 응답 DTO 리스트
     */
    private List<VCDetailResDto> processSourceFiles(
            List<VCDetailDto> srcFiles,
            List<MultipartFile> files,
            String voiceId,
            Long memberId) {

        // 업로드된 파일들을 파일 이름으로 매핑
        Map<String, MultipartFile> fileMap = files != null ? files.stream()
                .collect(Collectors.toMap(MultipartFile::getOriginalFilename, file -> file)) : new HashMap<>();
        log.debug("소스 파일 매핑: {}", fileMap.keySet());

        // 각 소스 파일에 대해 변환 작업 수행
        return srcFiles.stream()
                .map(srcFile -> {
                    try {
                        MultipartFile sourceAudio = null;
                        String sourceFileUrl = null;

                        // 우선 memberAudioMetaId를 통해 S3 파일을 처리
                        if (srcFile.getMemberAudioMetaId() != null) {
                            log.debug("S3 파일 처리 시도: memberAudioMetaId={}", srcFile.getMemberAudioMetaId());
                            // S3에서 파일 URL 조회
                            sourceFileUrl = memberAudioMetaRepository.findAudioUrlByAudioMetaId(
                                    srcFile.getMemberAudioMetaId(),
                                    AudioType.VC_SRC
                            );
                            if (sourceFileUrl == null) {
                                // S3 파일 누락 시 예외 발생
                                log.error("[S3 파일 누락] memberAudioMetaId: {}", srcFile.getMemberAudioMetaId());
                                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
                            }
                            log.debug("S3 파일 URL 조회 성공: {}", sourceFileUrl);
                        }
                        // memberAudioMetaId가 없을 경우에만 localFileName을 통해 로컬 파일을 처리
                        else if (srcFile.getLocalFileName() != null) {
                            log.debug("로컬 파일 처리 시도: localFileName={}", srcFile.getLocalFileName());
                            // 로컬 파일 이름에서 실제 파일 이름만 추출
                            String strippedFileName = Paths.get(srcFile.getLocalFileName()).getFileName().toString();
                            // 매핑된 파일 가져오기
                            sourceAudio = fileMap.get(strippedFileName);
                            if (sourceAudio == null) {
                                // 로컬 파일 누락 시 예외 발생
                                log.error("[로컬 파일 누락] 파일명: {}", strippedFileName);
                                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
                            }
                            log.debug("로컬 파일 매핑 성공: {}", strippedFileName);
                        } else {
                            // 소스 파일 정보가 모두 없을 경우 예외 발생
                            log.error("[소스 파일 누락] localFileName과 memberAudioMetaId가 모두 null");
                            throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA);
                        }

                        // 단일 소스 파일 처리
                        return processSingleSourceFile(srcFile, sourceAudio, sourceFileUrl, voiceId, memberId);
                    } catch (BusinessException e) {
                        // 이미 에러 메시지를 로깅했으므로 예외 재던지기
                        throw e;
                    } catch (Exception e) {
                        // 기타 예외 발생 시 로깅 후 예외 던지기
                        log.error("[소스 파일 처리 실패] srcFile ID: {}, 이유: {}", srcFile.getId(), e.getMessage(), e);
                        throw new BusinessException(ErrorCode.SERVER_ERROR);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 단일 소스 파일을 처리하고 변환하는 메서드입니다.
     * 로컬 파일이거나 S3에서 가져온 파일을 변환 후 S3에 업로드합니다.
     *
     * @param srcFile        소스 파일 요청 DTO
     * @param sourceAudio    로컬에서 업로드된 파일 (null일 수 있음)
     * @param sourceFileUrl  S3에서 가져온 파일 URL (null일 수 있음)
     * @param voiceId        Voice ID
     * @param memberId       회원 ID
     * @return 변환된 VC 디테일 응답 DTO
     */
    private VCDetailResDto processSingleSourceFile(
            VCDetailDto srcFile,
            MultipartFile sourceAudio,
            String sourceFileUrl,
            String voiceId,
            Long memberId) {

        File tempFile = null; // 로컬 파일을 임시로 저장할 파일
        File convertedFile = null; // 변환된 파일을 저장할 파일

        try {
            String inputFilePath;

            if (sourceAudio != null) {
                // 로컬 파일인 경우, 임시 파일로 저장
                String tempFilePath = uploadDir + File.separator + sourceAudio.getOriginalFilename();
                tempFile = new File(tempFilePath);
                sourceAudio.transferTo(tempFile); // MultipartFile을 File로 변환하여 저장
                inputFilePath = tempFile.getAbsolutePath();
                log.info("로컬 소스 오디오 저장 완료: {}", inputFilePath);
            } else if (sourceFileUrl != null) {
                // S3 파일인 경우, S3에서 다운로드하여 임시 파일로 저장
                log.debug("S3에서 파일 다운로드 시도: {}", sourceFileUrl);
                inputFilePath = s3Service.downloadFileFromS3(sourceFileUrl, uploadDir);
                log.info("S3 소스 오디오 다운로드 완료: {}", inputFilePath);
            } else {
                // 소스 파일 정보가 모두 없을 경우 예외 발생
                log.warn("소스 오디오를 찾을 수 없습니다.");
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
            }

            // 음성 변환 수행 (외부 API 호출)
            log.info("음성 변환 시작: voiceId={}, inputFilePath={}", voiceId, inputFilePath);
            String convertedFilePath = elevenLabsClient.convertSpeechToSpeech(voiceId, inputFilePath);
            log.info("음성 변환 완료: convertedFilePath={}", convertedFilePath);

            // 변환된 파일을 MultipartFile로 변환 (유틸리티 사용)

            convertedFile = new File(convertedFilePath);
            if (!convertedFile.exists()) {
                // 변환된 파일이 존재하지 않을 경우 예외 발생
                log.error("변환된 파일이 존재하지 않습니다: {}", convertedFilePath);
                throw new BusinessException(ErrorCode.FILE_CONVERSION_FAILED);
            }
            MultipartFile convertedMultipartFile = CommonFileUtils.convertFileToMultipartFile(
                    convertedFile, convertedFile.getName());
            log.debug("변환된 파일을 MultipartFile로 변환 완료: {}", convertedFile.getName());

            // 변환된 파일을 S3에 업로드하고 URL 반환
            log.debug("S3에 변환된 파일 업로드 시도: projectId={}, srcFileId={}", srcFile.getProjectId(), srcFile.getId());
            String uploadedUrl = s3Service.uploadUnitSaveFile(
                    convertedMultipartFile, memberId, srcFile.getProjectId(), srcFile.getId());
            log.info("변환된 파일 S3 업로드 완료: {}", uploadedUrl);

            // 변환된 파일의 정보를 담은 응답 DTO 생성
            VCDetailResDto vcDetailResDto = new VCDetailResDto(
                    srcFile.getId(),
                    srcFile.getProjectId(),
                    srcFile.getIsChecked(),
                    srcFile.getUnitScript(),
                    sourceAudio != null ? sourceAudio.getOriginalFilename() : sourceFileUrl,
                    List.of(uploadedUrl)
            );
            log.debug("VCDetailResDto 생성 완료: {}", vcDetailResDto);
            return vcDetailResDto;
        } catch (BusinessException e) {
            // 비즈니스 예외 발생 시, 이미 로깅했으므로 예외 재던지기
            throw e;
        } catch (Exception e) {
            // 기타 예외 발생 시 로깅 후 비즈니스 예외 던지기
            log.error("소스 파일 처리 실패: srcFile ID={}, 이유={}", srcFile.getId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        } finally {
            // 임시 파일 정리: 로컬 소스 파일 삭제
            if (tempFile != null && tempFile.exists()) {
                if (tempFile.delete()) {
                    log.info("임시 소스 파일 삭제 완료: {}", tempFile.getAbsolutePath());
                } else {
                    log.warn("임시 소스 파일 삭제 실패: {}", tempFile.getAbsolutePath());
                }
            }
            // 임시 파일 정리: 변환된 파일 삭제
            if (convertedFile != null && convertedFile.exists()) {
                if (convertedFile.delete()) {
                    log.info("변환된 파일 삭제 완료: {}", convertedFile.getAbsolutePath());
                } else {
                    log.warn("변환된 파일 삭제 실패: {}", convertedFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 프로젝트 상태를 업데이트하는 메서드입니다.
     * 모든 VC 디테일의 상태를 검사하여 프로젝트의 전체 상태를 설정합니다.
     *
     * @param projectId 프로젝트 ID
     */
    private void updateProjectStatus(Long projectId) {
        // 프로젝트 ID로 관련된 VC 디테일 조회
        List<VCDetail> details = vcDetailRepository.findByVcProject_Id(projectId);
        if (details.isEmpty()) {
            log.error("프로젝트 상태 업데이트 실패: VCDetail이 존재하지 않음, projectId={}", projectId);
            throw new BusinessException(ErrorCode.VC_DETAIL_NOT_FOUND);
        }
        log.debug("프로젝트 상태 업데이트 시작: projectId={}, VCDetail 개수={}", projectId, details.size());

        // 디테일 중 하나라도 실패한 상태인지 확인
        boolean hasFailure = details.stream()
                .flatMap(detail -> detail.getApiStatuses().stream())
                .anyMatch(status -> status.getApiUnitStatusConst() == APIUnitStatusConst.FAILURE);
        log.debug("프로젝트에 실패한 API 상태 존재 여부: {}", hasFailure);

        // 모든 디테일이 성공한 상태인지 확인
        boolean allSuccess = details.stream()
                .flatMap(detail -> detail.getApiStatuses().stream())
                .allMatch(status -> status.getApiUnitStatusConst() == APIUnitStatusConst.SUCCESS);
        log.debug("프로젝트에 모든 API 상태가 성공인지 여부: {}", allSuccess);

        // 프로젝트 정보 조회
        VCProject project = vcProjectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("프로젝트 상태 업데이트 실패: 프로젝트 조회 실패, projectId={}", projectId);
                    return new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
                });
        log.debug("프로젝트 조회 성공: projectId={}, currentStatus={}", projectId, project.getApiStatus());

        // 프로젝트 상태 업데이트
        if (hasFailure) {
            project.updateAPIStatus(APIStatusConst.FAILURE);
            log.info("프로젝트 상태 업데이트: FAILURE");
        } else if (allSuccess) {
            project.updateAPIStatus(APIStatusConst.SUCCESS);
            log.info("프로젝트 상태 업데이트: SUCCESS");
        } else {
            project.updateAPIStatus(APIStatusConst.NOT_STARTED);
            log.info("프로젝트 상태 업데이트: NOT_STARTED");
        }

        // 업데이트된 프로젝트 저장
        vcProjectRepository.save(project);
        log.debug("프로젝트 상태 저장 완료: projectId={}, newStatus={}", projectId, project.getApiStatus());
    }

    /**
     * MultipartFile을 임시 디렉토리에 저장하고 File 객체로 반환하는 메서드입니다.
     *
     * @param file MultipartFile 객체
     * @return 저장된 File 객체
     * @throws IOException 파일 저장 중 발생하는 예외
     */
    private File saveMultipartFileToTemp(MultipartFile file) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir"); // 시스템 임시 디렉토리 경로
        File tempFile = new File(tempDir, file.getOriginalFilename());
        file.transferTo(tempFile); // MultipartFile을 File로 변환하여 저장
        log.debug("임시 파일 저장 완료: {}", tempFile.getAbsolutePath());
        return tempFile;
    }

    /**
     * 업로드된 파일 리스트를 파일 이름을 키로 하는 Map으로 변환하는 메서드입니다.
     *
     * @param files 업로드된 MultipartFile 리스트
     * @return 파일 이름을 키로 하는 Map
     */
    private Map<String, MultipartFile> createFileMap(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            log.info("[업로드된 파일이 없습니다.]");
            return Map.of(); // 빈 Map 반환
        }
        // 파일 이름을 키로, MultipartFile을 값으로 하는 Map 생성
        Map<String, MultipartFile> fileMap = files.stream()
                .collect(Collectors.toMap(MultipartFile::getOriginalFilename, file -> file));
        log.debug("파일 매핑 완료: {}", fileMap.keySet());
        return fileMap;
    }
}
