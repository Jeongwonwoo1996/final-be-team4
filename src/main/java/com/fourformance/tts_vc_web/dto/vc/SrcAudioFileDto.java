package com.fourformance.tts_vc_web.dto.vc;

import com.fourformance.tts_vc_web.common.constant.AudioType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SrcAudioFileDto {
    private Long detailId;
    private String localFileName; // 로컬 업로드 파일의 이름 (MultipartFile 매칭)
    private String unitScript; // 선택 사항: 텍스트 스크립트 (nullable)
    private Boolean isChecked; // 체크 표시 여부
}
