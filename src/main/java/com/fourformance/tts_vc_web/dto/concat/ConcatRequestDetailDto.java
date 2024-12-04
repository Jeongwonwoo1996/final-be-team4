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

    private Long id; // concatDetail ID
    private Long memberAudioId; // S3 파일이 업로드된 memberAudioMeta ID
    private String localFileName; // 로컬 파일 매칭 여부
    private Integer audioSeq;
    private boolean isChecked;
    private String unitScript;
    private Float endSilence;
    private MultipartFile sourceAudio;
}
