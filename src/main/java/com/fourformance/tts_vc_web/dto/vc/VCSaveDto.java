package com.fourformance.tts_vc_web.dto.vc;

import com.fourformance.tts_vc_web.common.constant.APIStatusConst;
import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.MemberAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.MemberAudioVC;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VCSaveDto {

    // VCProject 관련 필드
    private Long projectId;
    private String projectName;

    private List<SrcAudioFileDto> srcFiles; // 소스 오디오 파일 리스트
    private TrgAudioFileDto trgFile; // 타겟 오디오 파일 리스트 => 생각해보니까 얘는 리스트로 둘 필요가 없는디...

    //    private String trgVoiceId; // 타겟 음성 ID, 필요하면 주석 풀어야지...


}
