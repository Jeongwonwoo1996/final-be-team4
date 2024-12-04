package com.fourformance.tts_vc_web.dto.tts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fourformance.tts_vc_web.common.constant.APIStatusConst;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TTSSaveDto {
    private Long projectId;
    private String projectName;
    private Long globalVoiceStyleId;
    private String fullScript;
    private Float globalSpeed;
    private Float globalPitch;
    private Float globalVolume;

    private List<TTSSaveDetailDto> ttsDetails;
}