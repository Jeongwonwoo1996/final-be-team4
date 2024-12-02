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

        // vcDetailDtos에서 하나씩 꺼내서 Task 생성하고 큐에 넣기
        for (VCDetailDto detail : vcDetailDtos) {
            // dto를 문자열 json으로 변환
            String detailJson = convertDetailToJson(detail);

            // Task 생성 및 저장
            Task task = Task.createTask(vcProject, ProjectType.VC, detailJson);
            taskRepository.save(task);

            // 메시지 생성 및 RabbitMQ에 전송
            VCMsgDto message = createVCMsgDto(detail, task.getId(), memberId, vcProject.getTrgVoiceId());
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
}