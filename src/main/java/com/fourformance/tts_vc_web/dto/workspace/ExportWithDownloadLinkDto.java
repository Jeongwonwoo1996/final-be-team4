package com.fourformance.tts_vc_web.dto.workspace;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportWithDownloadLinkDto {
    private Long metaId;
    private Long projectId;
    private String fileName;
    private String downloadLink; // Presigned URL
    private String unitStatus;
    private String projectName;
    private String projectType;
    private String script;
    private LocalDateTime createdAt;
}