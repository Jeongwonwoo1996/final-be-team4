package com.fourformance.tts_vc_web.dto.tts;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TTSRequestDetailDto {

    private Long id; // 상세 정보 ID
    private Long projectId; // 프로젝트 ID
    private String unitScript; // 단위 스크립트
    private Float unitSpeed; // 단위 속도
    private Float unitPitch; // 단위 피치
    private Float unitVolume; // 단위 볼륨
    private Boolean isDeleted; // 삭제 여부
    private Integer unitSequence; // 단위 시퀀스
    private Long UnitVoiceStyleId; // 스타일 이름 (optional, lazy load 대신 포함할 수 있는 필드)
}
