package com.fourformance.tts_vc_web.dto.concat;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Long memberAudioId; // S3 파일이 업로드된 memberAudioMeta ID
    private String localFileName; // 로컬 파일명
    private Integer audioSeq;
    private boolean isChecked;
    private String unitScript;
    private Float endSilence;
    @JsonIgnore
    private MultipartFile sourceAudio; // 업로드된 파일 (optional)
}
