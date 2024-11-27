package com.fourformance.tts_vc_web.dto.workspace;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectListDto {
    private Long projectId;          // 프로젝트 ID
    private String projectType;      // 프로젝트 타입 (TTS, VC, CONCAT 등)
    private String projectName;      // 프로젝트 이름
    private String script;           // 프로젝트 내용 (첫 번째 스크립트 기준)
    private String projectStatus;    // 프로젝트 상태
    private LocalDateTime updatedAt; // 업데이트 날짜
    private LocalDateTime createdAt; // 생성된 날짜
}