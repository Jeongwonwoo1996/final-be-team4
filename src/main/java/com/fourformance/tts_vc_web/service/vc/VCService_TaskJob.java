package com.fourformance.tts_vc_web.service.vc;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourformance.tts_vc_web.common.constant.APIStatusConst;
import com.fourformance.tts_vc_web.common.constant.APIUnitStatusConst;
import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.common.constant.ProjectType;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.common.util.CommonFileUtils;
import com.fourformance.tts_vc_web.common.util.ElevenLabsClient_team_api;
import com.fourformance.tts_vc_web.domain.entity.*;
import com.fourformance.tts_vc_web.dto.common.TTSMsgDto;
import com.fourformance.tts_vc_web.dto.common.VCMsgDto;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.tts.TTSRequestDetailDto;
import com.fourformance.tts_vc_web.dto.vc.*;
import com.fourformance.tts_vc_web.repository.*;
import com.fourformance.tts_vc_web.service.common.S3Service;
import com.fourformance.tts_vc_web.service.common.TaskProducer;
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

    private final MemberRepository memberRepository;
    private final VCProjectRepository vcProjectRepository;
    private final VCDetailRepository vcDetailRepository;
    private final VCService_team_multi vcService;
    private final MemberAudioMetaRepository memberAudioMetaRepository;
    private final ObjectMapper objectMapper;
    private final TaskRepository taskRepository;
    private final TaskProducer taskProducer;


    @Transactional
    public void enqueueVCTasks(VCSaveRequestDto vcReqDto, List<MultipartFile> files, Long memberId) {
        // 프로젝트 저장, 디테일 저장
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // Step 2: VC 프로젝트 저장 및 ID 반환, VcDetail 저장
        Long projectId = vcService.saveVCProject(vcReqDto, files, member);
        if (projectId == null) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }
        // vcProject 객체 반환
        VCProject vcProject = vcProjectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VC_PROJECT_NOT_FOUND));

        // Step 3: 프로젝트 ID로 연관된 VC 디테일(src) 조회
        List<VCDetail> vcDetails = vcDetailRepository.findByVcProject_Id(projectId);

        // Step 4: VC 디테일 DTO 변환 및 필터링 (체크된 항목만)
        List<VCDetailDto> vcDetailDtos = vcDetails.stream()
                .filter(vcDetail -> vcDetail.getIsChecked() && !vcDetail.getIsDeleted())
                .map(VCDetailDto::createVCDetailDtoWithLocalFileName)
                .collect(Collectors.toList());

        // Step 5: 저장된 타겟(TRG) 오디오 정보 가져오기
        MemberAudioMeta memberAudio = memberAudioMetaRepository.findSelectedAudioByTypeAndMember(AudioType.VC_TRG, memberId);

        String voiceId;
        if (memberAudio != null) {
            // Step 6: 타겟 오디오로 Voice ID 생성
            voiceId = processTargetFiles(vcReqDto.getTrgFiles(), memberAudio);

        }else{
            voiceId = memberAudioMetaRepository.findtrgVoiceIdById(vcReqDto.getTrgFiles().get(0).getS3MemberAudioMetaId());
        }

        // Step 7: VC 프로젝트에 trg_voice_id 업데이트
        updateProjectTargetVoiceId(projectId, voiceId);

        // vcDetailDtos에서 하나씩 꺼내서 Task 생성하고 큐에 넣기
        for (VCDetailDto detail : vcDetailDtos) {
            // dto를 문자열 json으로 변환
            String detailJson = convertDetailToJson(detail);

            // Task 생성 및 저장
            Task task = Task.createTask(vcProject, ProjectType.VC, detailJson);
            taskRepository.save(task);

            // 메시지 생성 및 RabbitMQ에 전송
            VCMsgDto message = createVCMsgDto(detail, task.getId(), memberId, voiceId);
            taskProducer.sendTask("AUDIO_VC", message);
        }
    }

    /**
     * dto를 문자열 json으로 변환
     *
     * @param detailDto
     * @return
     */
    private String convertDetailToJson(VCDetailDto detailDto) {
        try {
            return objectMapper.writeValueAsString(detailDto);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_PROCESSING_ERROR);
        }
    }

    /**
     * VC 메시지 DTO 생성
     * @param detail
     * @param taskId
     * @return
     */
    private VCMsgDto createVCMsgDto(VCDetailDto detail, Long taskId, Long memberId, String trgId) {
        return VCMsgDto.builder()
                .memberId(memberId)
                .taskId(taskId)          // 생성된 Task ID
                .projectId(detail.getProjectId())
                .detailId(detail.getId()) // detailId
                .memberAudioId(detail.getMemberAudioMetaId())
                .trgVoiceId(trgId)
                .unitScript(detail.getUnitScript())
                .localFileName(detail.getLocalFileName())
                .build();
    }

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

            // Voice ID를 MemberAudioMeta에 업데이트
            memberAudio.update(voiceId);
            memberAudioMetaRepository.save(memberAudio);

            return voiceId;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
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
    }
}