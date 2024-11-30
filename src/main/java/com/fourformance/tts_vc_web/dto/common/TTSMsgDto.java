package com.fourformance.tts_vc_web.dto.common;

import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.common.constant.ProjectType;
import com.fourformance.tts_vc_web.dto.tts.TTSRequestDetailDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TTSMsgDto {
    private Long taskId; // 작업을 추적하고 상태를 관리하기 위함
    private Long memberId; // 요청한 회원 id 필요한가...
    private TTSRequestDetailDto ttsDetail;
    private LocalDateTime timestamp; // 요청시간

}