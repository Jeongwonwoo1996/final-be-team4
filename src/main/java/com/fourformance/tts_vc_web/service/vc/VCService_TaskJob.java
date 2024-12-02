package com.fourformance.tts_vc_web.service.vc;


import com.fourformance.tts_vc_web.common.constant.APIStatusConst;
import com.fourformance.tts_vc_web.common.constant.APIUnitStatusConst;
import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.common.util.CommonFileUtils;
import com.fourformance.tts_vc_web.common.util.ElevenLabsClient_team_api;
import com.fourformance.tts_vc_web.domain.entity.*;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.vc.*;
import com.fourformance.tts_vc_web.repository.*;
import com.fourformance.tts_vc_web.service.common.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
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

    @Transactional
    public void enqueueVCTasks(VCSaveRequestDto vcReqDto, Long memberId){
        // 프로젝트 저장


        // 디테일별 작업 처리
            // 디테일 저장
            // 엔티티를 dto로 변환
            // 디테일 dto를 json으로 변환
            // Task 생성 및 저장
            // 메시지 생성 및 RabbitMQ에 전송
        try {
            // VC 프로젝트 처리
            List<VCDetailResDto> response = processVCProject(VCSaveRequestDto, files, memberId);

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

    public Long saveVCProject(VCSaveRequestDto vcSaveDto, List<MultipartFile> localFiles, Member member) {

        memberAudioMetaRepository.resetSelection(AudioType.VC_TRG);

        // 1. VCProject 생성/업데이트
        VCProject vcProject = vcSaveDto.getProjectId() == null
                ? createNewVCProject(vcSaveDto, member)
                : updateExistingVCProject(vcSaveDto); // member는 안넘겨도 될 것 같음

        // 2. 타겟 파일 처리
        processFiles(vcSaveDto.getTrgFiles(), localFiles, vcProject, AudioType.VC_TRG);

        // 3. 소스 파일 처리
        processFiles(vcSaveDto.getSrcFiles(), localFiles, vcProject, AudioType.VC_SRC);

        return vcProject.getId();
    }

    // 메서드 오버로딩 - 재홍
    private VCProject createNewVCProject(VCSaveRequestDto vcSaveDto, Member member) {

        VCProject vcProject = VCProject.createVCProject(member, vcSaveDto.getProjectName());
        vcProjectRepository.save(vcProject);
        return vcProject;
    }

    //메서드 오버로딩 - 재홍
    private VCProject updateExistingVCProject(VCSaveRequestDto vcSaveDto) {
        VCProject vcProject = vcProjectRepository.findById(vcSaveDto.getProjectId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT));

        vcProject.updateVCProject(vcSaveDto.getProjectName(), null);

        return vcProject;
    }

    private void processFiles(List<? extends AudioFileDto> fileDtos, List<MultipartFile> files, VCProject vcProject, AudioType audioType) {

        if (fileDtos == null || fileDtos.isEmpty()) { // 업로드 된 파일이 없을 때
            return;
        }

        for (AudioFileDto fileDto : fileDtos) {
            MemberAudioMeta audioMeta = null;

            // trg audio면 s3에서 목록 선택할 수 있음
            if (fileDto instanceof TrgAudioFileRequestDto) {
                // S3 파일 처리
                Long s3Id = ((TrgAudioFileRequestDto) fileDto).getS3MemberAudioMetaId();
                if (s3Id != null) {
                    audioMeta = memberAudioMetaRepository.findById(s3Id)
                            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_AUDIO));
                }
            }
            // 로컬 파일 처리
            String localFileName = fileDto.getLocalFileName();

            if (localFileName != null) {
                MultipartFile localFile = findMultipartFileByName(files, localFileName);
                String uploadUrl = s3Service.uploadAndSaveMemberFile(localFile,vcProject.getMember().getId(), vcProject.getId(), audioType);


                audioMeta = memberAudioMetaRepository.findFirstByAudioUrl(uploadUrl)
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_AUDIO));
                memberAudioMetaRepository.selectAudio(audioMeta.getId(), AudioType.VC_TRG);
            }


            if (audioMeta == null) {
                throw new BusinessException(ErrorCode.INVALID_PROJECT_DATA);
            }

            if (audioType == AudioType.VC_TRG) {
                // 타겟 파일은 VCProject에 저장
                vcProject.updateVCProject(vcProject.getProjectName(), audioMeta);
            } else {
                // 소스 파일은 VCDetail에 저장
                VCDetail vcDetail = VCDetail.createVCDetail(vcProject, audioMeta);
                SrcAudioFileRequestDto srcFile = (SrcAudioFileRequestDto) fileDto;
                vcDetail.updateDetails(srcFile.getIsChecked(), srcFile.getUnitScript());
                vcDetailRepository.save(vcDetail);
            }
        }
    }

    /**
     * VC 프로젝트 처리 메서드
     * 1. 멤버 검증
     * 2. VC 프로젝트 저장 및 ID 반환
     * 3. VC 디테일 정보 조회 및 처리
     * 4. 프로젝트 상태 업데이트
     */
    public List<VCDetailResDto> processVCProject(VCSaveRequestDto VCSaveRequestDto, List<MultipartFile> files, Long memberId) {
        LOGGER.info("[VC 프로젝트 시작]");

        // Step 1: 멤버 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // Step 2: VC 프로젝트 저장 및 ID 반환
        Long projectId = vcService.saveVCProject(VCSaveRequestDto, files, member);
        if (projectId == null) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }
        LOGGER.info("[VC 프로젝트 저장 완료] 프로젝트 ID: " + projectId);

        // Step 3: 프로젝트 ID로 연관된 VC 디테일 조회
        List<VCDetail> vcDetails = vcDetailRepository.findByVcProject_Id(projectId);
        LOGGER.info("[VC 디테일 조회 완료] 디테일 개수: " + vcDetails.size());

        // Step 4: VC 디테일 DTO 변환 및 필터링 (체크된 항목만)
        List<VCDetailDto> vcDetailDtos = vcDetails.stream()
                .filter(vcDetail -> vcDetail.getIsChecked() && !vcDetail.getIsDeleted())
                .map(VCDetailDto::createVCDetailDtoWithLocalFileName)
                .collect(Collectors.toList());
        LOGGER.info("[VC 디테일 필터링 완료] 체크된 디테일 개수: " + vcDetailDtos.size());

        // Step 5: 저장된 타겟(TRG) 오디오 정보 가져오기
        MemberAudioMeta memberAudio = memberAudioMetaRepository.findSelectedAudioByTypeAndMember(AudioType.VC_TRG, memberId);
        if (memberAudio == null) {
            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
        }
        LOGGER.info("[타겟 오디오 조회 완료] 오디오 ID: " + memberAudio.getId());

        // Step 6: 타겟 오디오로 Voice ID 생성

        String voiceId = processTargetFiles(VCSaveRequestDto.getTrgFiles(), memberAudio);

        LOGGER.info("[Voice ID 생성 완료] Voice ID: " + voiceId);

        // Step 7: VC 프로젝트에 trg_voice_id 업데이트
        updateProjectTargetVoiceId(projectId, voiceId);

        // Step 8: 소스(SRC) 파일 처리
        List<VCDetailResDto> vcDetailsRes = processSourceFiles(files, vcDetailDtos, voiceId, memberId);

        // Step 9: 프로젝트 상태 업데이트
        updateProjectStatus(projectId);
        LOGGER.info("[VC 프로젝트 상태 업데이트 완료] 프로젝트 ID: " + projectId);

        return vcDetailsRes;
    }

    /**
     * 타겟 오디오 파일 처리 및 Voice ID 생성 -> 월 한도 돌아오면 사용
     */
//    private String processTargetFiles(List<TrgAudioFileRequestDto> trgFiles, MemberAudioMeta memberAudio) {
//        if (trgFiles == null || trgFiles.isEmpty()) {
//            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
//        }
//        try {
//            // Step 1: Target 파일 URL 확인
//            if (memberAudio == null || memberAudio.getAudioUrl() == null) {
//                throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
//            }
//            String targetFileUrl = memberAudio.getAudioUrl();
//            LOGGER.info("[타겟 오디오 업로드 시작] URL: " + targetFileUrl);
//
//            // Step 2: Voice ID 생성
//            String voiceId = elevenLabsClient.uploadVoice(targetFileUrl);
//            LOGGER.info("[Voice ID 생성 완료] Voice ID: " + voiceId);
//
//            // Step 3: Voice ID 저장
//            memberAudio.update(voiceId);
//            memberAudioMetaRepository.save(memberAudio);
//            LOGGER.info("[MemberAudioMeta 업데이트 완료] Voice ID: " + voiceId);
//
//            return voiceId;
//        } catch (IOException e) {
//            LOGGER.severe("[Voice ID 생성 실패] " + e.getMessage());
//            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
//        }
//    }

    /**
     * 타겟 오디오 파일 처리 및 Voice ID 생성 ->  월 한도 제한으로 하드코딩 함
     */
    private String processTargetFiles(List<TrgAudioFileRequestDto> trgFiles, MemberAudioMeta memberAudio) {
        if (trgFiles == null || trgFiles.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
        }
        try {
            // 하드코딩된 Voice ID 사용
            String voiceId = "DNSy71aycodz7FWtd91e"; // 테스트용 하드코딩
            LOGGER.info("[Voice ID 하드코딩 적용] Voice ID: " + voiceId);

            // Voice ID를 MemberAudioMeta에 업데이트
            memberAudio.update(voiceId);
            memberAudioMetaRepository.save(memberAudio);
            LOGGER.info("[MemberAudioMeta 업데이트 완료] Voice ID: " + voiceId);

            return voiceId;
        } catch (Exception e) {
            LOGGER.severe("[타겟 파일 처리 실패] " + e.getMessage());
            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }


    /**
     * 소스 파일 처리 및 변환
     */
    private List<VCDetailResDto> processSourceFiles(
            List<MultipartFile> inputFiles,
            List<VCDetailDto> srcFiles,
            String voiceId,
            Long memberId) {
        return srcFiles.stream()
                .map(srcFile -> {
                    // Step 1: 소스 파일 매칭
                    MultipartFile matchingFile = findMultipartFileByName(inputFiles, srcFile.getLocalFileName());
                    LOGGER.info("[소스 파일 매칭] 파일명: " + (matchingFile != null ? matchingFile.getOriginalFilename() : "null"));

                    // Step 2: 매칭된 파일 변환
                    if (matchingFile != null) {
                        // API 상태 기록 생성
                        String requestPayload = String.format("Voice ID: %s, Source File: %s", voiceId, srcFile.getLocalFileName());
                        VCDetail vcDetail = vcDetailRepository.findById(srcFile.getId())
                                .orElseThrow(() -> new BusinessException(ErrorCode.VC_DETAIL_NOT_FOUND));
                        APIStatus apiStatus = APIStatus.createAPIStatus(vcDetail, null, requestPayload);
                        apiStatusRepository.save(apiStatus);

                        try {
                            // 변환 작업 수행
                            VCDetailResDto result = processSingleSourceFile(srcFile, matchingFile, voiceId, memberId);

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
                            throw e;
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private VCDetailResDto processSingleSourceFile(
            VCDetailDto srcFile,
            MultipartFile originFile,
            String voiceId,
            Long memberId) {
        String convertedFilePath = null;
        File convertedFile = null;
        try {
            // Step 1: 소스 파일 URL 가져오기
            String sourceFileUrl = memberAudioMetaRepository.findAudioUrlsByAudioMetaIds(
                    srcFile.getMemberAudioMetaId(),
                    AudioType.VC_SRC
            );
            LOGGER.info("[소스 파일 URL 조회] URL: " + sourceFileUrl);

            // Step 2: 변환 작업 수행
            convertedFilePath = elevenLabsClient.convertSpeechToSpeech(voiceId, sourceFileUrl);
            LOGGER.info("[파일 변환 완료] 파일 경로: " + convertedFilePath);

            // Step 3: 변환된 파일 읽기 및 S3 저장
            convertedFile = new File(convertedFilePath);
            byte[] convertedFileBytes = Files.readAllBytes(convertedFile.toPath());

            MultipartFile convertedMultipartFile = CommonFileUtils.convertFileToMultipartFile(convertedFile, convertedFile.getName());
            String vcOutputUrl = s3Service.uploadUnitSaveFile(convertedMultipartFile, memberId, srcFile.getProjectId(), srcFile.getId());
            LOGGER.info("[S3 업로드 완료] URL: " + vcOutputUrl);

            // Step 4: 결과 DTO 생성 및 반환
            return new VCDetailResDto(
                    srcFile.getId(),
                    srcFile.getProjectId(),
                    srcFile.getIsChecked(),
                    srcFile.getUnitScript(),
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
     * VC 프로젝트에 trg_voice_id 업데이트
     */
    private void updateProjectTargetVoiceId(Long projectId, String trgVoiceId) {
        VCProject vcProject = vcProjectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));
        vcProject.updateTrgVoiceId(trgVoiceId);
        vcProjectRepository.save(vcProject);
        LOGGER.info("[VC 프로젝트 업데이트] trg_voice_id: " + trgVoiceId);
    }

    /**
     * VC 프로젝트 상태 업데이트
     */
    private void updateProjectStatus(Long projectId) {
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

    /**
     * 업로드한 파일에서 파일 이름 찾기
     */
    private MultipartFile findMultipartFileByName(List<MultipartFile> files, String fileName) {
        if (fileName == null) {
            LOGGER.warning("[파일 찾기 실패] 파일 이름이 null입니다.");
            return null;
        }
        String simpleFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        return files.stream()
                .filter(file -> file.getOriginalFilename().equals(simpleFileName))
                .findFirst()
                .orElse(null);
    }
}

