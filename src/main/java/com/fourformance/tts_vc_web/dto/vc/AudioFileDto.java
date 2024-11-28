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
public class AudioFileDto {
//    private AudioType audioType; // 오디오 타입 (VC_TRG 또는 VC_SRC)
    private String localFileName; // 로컬 업로드 파일의 이름 (MultipartFile 매칭)
}
