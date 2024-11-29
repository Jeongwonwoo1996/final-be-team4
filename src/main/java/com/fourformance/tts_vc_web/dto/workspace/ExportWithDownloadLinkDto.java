package com.fourformance.tts_vc_web.dto.workspace;

import com.fourformance.tts_vc_web.common.constant.APIUnitStatusConst;
import com.fourformance.tts_vc_web.common.constant.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportWithDownloadLinkDto {
    private Long metaId;
    private String fileName;
    private String downloadLink; // Presigned URL
    private String unitStatus;
    private String projectName;
    private String projectType;
    private String script;
    private LocalDateTime createdAt;
}