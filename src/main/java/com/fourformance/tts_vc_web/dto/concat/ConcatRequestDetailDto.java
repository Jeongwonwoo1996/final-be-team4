package com.fourformance.tts_vc_web.dto.concat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConcatRequestDetailDto {

    private Long id;
    private String localFileName;
    private Integer audioSeq;
    private boolean isChecked;
    private String unitScript;
    private Float endSilence;
    private MultipartFile sourceAudio;
}
