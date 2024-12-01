package com.fourformance.tts_vc_web.dto.common;

import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.common.constant.ProjectType;
import com.fourformance.tts_vc_web.domain.entity.TTSDetail;
import com.fourformance.tts_vc_web.dto.tts.TTSRequestDetailDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TTSMsgDto {
    private Long taskId;            // 작업을 추적하고 상태를 관리하기 위함
    private Long memberId;          // 요청한 회원 id 필요한가...
    private Long detailId;          // 디테일 ID
    private Long projectId;         // 프로젝트 ID
    private String unitScript;      // 단위 스크립트
    private Float unitSpeed;        // 단위 속도
    private Float unitPitch;        // 단위 피치
    private Float unitVolume;       // 단위 볼륨
    private Long unitVoiceStyleId;  // 스타일 ID
    private TTSRequestDetailDto ttsDetail;
    private LocalDateTime timestamp;
}