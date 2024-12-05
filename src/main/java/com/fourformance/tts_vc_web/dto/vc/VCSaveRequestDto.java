package com.fourformance.tts_vc_web.dto.vc;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VCSaveRequestDto {

    // VCProject 관련 필드
    private Long projectId;
    private String projectName;

    private List<SrcAudioFileRequestDto> srcFiles; // 소스 오디오 파일 리스트
    private List<TrgAudioFileRequestDto> trgFiles; // 타겟 오디오 파일 리스트 => 생각해보니까 얘는 리스트로 둘 필요가 없는디...
}
