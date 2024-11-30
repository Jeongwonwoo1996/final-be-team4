package com.fourformance.tts_vc_web.dto.workspace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportListDto {

    Long outputAudioMetaId;
    String fileName;
    String url; // generatedPresignedUrL로 만들어전달 // 버킷 루트로 전달예정.
    String unitStatus;
    String projectName;
    String projectType;
    String script;
    LocalDateTime createAt;

    @JsonIgnore
    String bucketRoute;

    public ExportListDto(Long outputAudioMetaId, String projectType, String projectName, String fileName, String script,
                         String unitStatus, LocalDateTime createAt, String url) {
        this.outputAudioMetaId = outputAudioMetaId;
        this.projectType = projectType;
        this.projectName = projectName;
        this.fileName = fileName;
        this.script = script;
        this.unitStatus = unitStatus;
        this.createAt = createAt;
        this.url = url;

    }
}
