package com.fourformance.tts_vc_web.dto.workspace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fourformance.tts_vc_web.service.common.S3Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportListDto {

    Long outputAudioMetaId;
    String projectType;
    String projectName;
    String fileName;
    String script;
    String unitStatus;
    String downloadLink; // generatedPresignedUrL로 만들어전달 // 버킷 루트로 전달예정.
    LocalDateTime updateAt;
    @JsonIgnore
    private String bucketRoute; // 내부적으로 presigned URL 생성을 위해 사용, JSON에는 노출되지 않음

    public ExportListDto(Long outputAudioMetaId, String projectType, String projectName, String fileName, String script,
                         String unitStatus, LocalDateTime updateAt, String downloadLink) {
        this.outputAudioMetaId = outputAudioMetaId;
        this.projectType = projectType;
        this.projectName = projectName;
        this.fileName = fileName;
        this.script = script;
        this.unitStatus = unitStatus;
        this.updateAt = updateAt;
        this.downloadLink = downloadLink;

    }
}
