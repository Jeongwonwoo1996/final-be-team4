package com.fourformance.tts_vc_web.dto.tts;

import com.fourformance.tts_vc_web.common.constant.APIUnitStatusConst;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TTSResponseDetailDto {

    private Long id; // 상세 정보 ID
    private Long projectId; // 프로젝트 ID
    private String unitScript; // 단위 스크립트
    private Float unitSpeed; // 단위 속도
    private Float unitPitch; // 단위 피치
    private Float unitVolume; // 단위 볼륨
    private Boolean isDeleted; // 삭제 여부
    private Integer unitSequence; // 단위 시퀀스
    private Long UnitVoiceStyleId; // 스타일 이름 (optional, lazy load 대신 포함할 수 있는 필드)
    private String fileUrl; // aws s3 업로드 경로
//    private String errorMessage; // 실패 시 오류 메시지
    private APIUnitStatusConst apiUnitStatus; // SUCCESS 또는 FAILURE
}
