package com.fourformance.tts_vc_web.dto.tts;

import com.fourformance.tts_vc_web.common.constant.APIStatusConst;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TTSResponseDto {
    private Long projectId;
    private String projectName;
    private Long globalVoiceStyleId;
    private String fullScript;
    private Float globalSpeed;
    private Float globalPitch;
    private Float globalVolume;
    private APIStatusConst apiStatus;
    private List<TTSResponseDetailDto> ttsDetails;
}
