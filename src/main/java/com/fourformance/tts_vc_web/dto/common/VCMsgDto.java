package com.fourformance.tts_vc_web.dto.common;

import com.fourformance.tts_vc_web.dto.tts.TTSRequestDetailDto;
import com.fourformance.tts_vc_web.dto.vc.VCSaveRequestDto;
import jakarta.mail.Multipart;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
public class VCMsgDto {
    private Long memberId;          // 회원 ID
    private Long taskId;            // 작업을 추적하고 상태를 관리하기 위함
    private Long projectId;         // 프로젝트 ID
    private Long detailId;          // 디테일 ID
    private Long MemberAudioId;     // src 오디오 ID
    private Long trgVoiceId;        // 타겟 보이스 ID
}