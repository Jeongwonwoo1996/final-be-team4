package com.fourformance.tts_vc_web.dto.common;

import com.fourformance.tts_vc_web.dto.tts.TTSRequestDetailDto;
import com.fourformance.tts_vc_web.dto.vc.VCSaveRequestDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VCMsgDto {
    private Long taskId;            // 작업을 추적하고 상태를 관리하기 위함
    private Long memberId;          // 요청한 회원 id 필요한가...
    private Long detailId;          // 디테일 ID
    private Long projectId;         // 프로젝트 ID
    private Long targetId;
    private Long targetURL;
    private Long MemberAudio;
    private VCSaveRequestDto vcDetail;
    private LocalDateTime timestamp;
}