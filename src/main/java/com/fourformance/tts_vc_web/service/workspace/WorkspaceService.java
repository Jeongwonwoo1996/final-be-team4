package com.fourformance.tts_vc_web.service.workspace;

import com.fourformance.tts_vc_web.common.constant.APIStatusConst;
import com.fourformance.tts_vc_web.common.constant.APIUnitStatusConst;
import com.fourformance.tts_vc_web.common.constant.ProjectType;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.APIStatus;
import com.fourformance.tts_vc_web.domain.entity.OutputAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.Project;
import com.fourformance.tts_vc_web.domain.entity.TTSProject;
import com.fourformance.tts_vc_web.domain.entity.VCProject;
import com.fourformance.tts_vc_web.dto.workspace.RecentExportDto;
import com.fourformance.tts_vc_web.dto.workspace.RecentProjectDto;
import com.fourformance.tts_vc_web.repository.OutputAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.ProjectRepository;
import com.fourformance.tts_vc_web.service.common.S3Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final ProjectRepository projectRepository;
    private final OutputAudioMetaRepository outputAudioMetaRepository;
    private final S3Service s3Service;

    public List<RecentProjectDto> getRecentProjects(Long memberId) {
        // memberId가 null이면 예외 발생
        if (memberId == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // DB에서 최신 5개의 프로젝트 조회
        List<Project> projects = projectRepository.findTop5ByMemberIdOrderByCreatedAtDesc(memberId);

        // 프로젝트 리스트를 DTO로 변환
        return projects.stream().map(project -> {
            String type = convertProjectType(project); // 프로젝트 타입 결정

            APIStatusConst apiStatus = null;

            // API 상태를 TTSProject와 VCProject에 따라 가져옴
            if (project instanceof TTSProject) {
                apiStatus = ((TTSProject) project).getApiStatus();
            } else if (project instanceof VCProject) {
                apiStatus = ((VCProject) project).getApiStatus();
            }

            return new RecentProjectDto(
                    project.getId(),
                    type,
                    project.getProjectName(),
                    apiStatus, // 상태는 TTSProject와 VCProject만 포함, 나머지는 null
                    project.getCreatedAt(),
                    project.getUpdatedAt()
            );
        }).collect(Collectors.toList());
    }

    private String convertProjectType(Project project) {
        // 프로젝트의 클래스 이름을 기반으로 적절한 타입 문자열로 변환
        String simpleName = project.getClass().getSimpleName();
        switch (simpleName) {
            case "TTSProject":
                return "TTS";
            case "VCProject":
                return "VC";
            case "ConcatProject":
                return "Concat";
            default:
                throw new BusinessException(ErrorCode.UNSUPPORTED_PROJECT_TYPE); // 지원하지 않는 타입 처리
        } //
    }

    /**
     * -테스트
     * @Test
     * @Transactional
     * void testFindTop5ByMemberIdWithNotDeletedProjects() {
     *     Long memberId = 1L; // 테스트에 사용할 회원 ID
     *
     *     List<Project> projects = projectRepository.findTop5ByMemberIdOrderByCreatedAtDesc(memberId);
     *
     *     assertNotNull(projects);
     *     assertTrue(projects.size() <= 5);
     *     for (Project project : projects) {
     *         assertFalse(project.getIsDeleted()); // 삭제되지 않은 프로젝트만 포함
     *     }
     * }
     *
     */


    /**
     * 최신 5개의 Export 작업 내역 조회
     */

    public List<RecentExportDto> getRecentExports(Long memberId) {

        if (memberId == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 최신 5개의 OutputAudioMeta 레코드 조회
        List<OutputAudioMeta> recentExports = outputAudioMetaRepository.findTop5ByMemberId(memberId);

        // DTO로 변환
        return recentExports.stream()
                .map(this::mapToRecentExportDto)
                .collect(Collectors.toList());

    }

    private RecentExportDto mapToRecentExportDto(OutputAudioMeta meta) {
        RecentExportDto dto = new RecentExportDto(); // DTO를 만들고

        // 공통설정
        dto.setMetaId(meta.getId()); // 메타아이디를 넣는거는 상관없음
        dto.setFileName(extractFileName(meta.getBucketRoute()));
//        dto.setBucketRoute(meta.getBucketRoute());
        dto.setUrl(s3Service.generatePresignedUrl(meta.getBucketRoute()));
        dto.setUnitStatus(getLatestUnitStatusFromMeta(meta));

        if (meta.getTtsDetail() != null) {
            dto.setProjectName(meta.getTtsDetail().getTtsProject().getProjectName());
            dto.setProjectType(ProjectType.TTS);
            dto.setScript(meta.getTtsDetail().getUnitScript());

        } else if (meta.getVcDetail() != null) {
            dto.setProjectName(meta.getVcDetail().getVcProject().getProjectName());
            dto.setProjectType(ProjectType.VC);
            dto.setScript(meta.getVcDetail().getUnitScript());

        } else if (meta.getConcatProject() != null) {
            List<String> scripts = outputAudioMetaRepository.findConcatDetailScriptsByOutputAudioMetaId(meta.getId());
            String combinedScripts = String.join(" ", scripts);
            dto.setProjectType(ProjectType.CONCAT);
            dto.setProjectName(meta.getConcatProject().getProjectName());
            dto.setScript(combinedScripts); // 조인해야지 볼 수 있음.
        }

        return dto;
    }

    private String extractFileName(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        return filePath.substring(filePath.lastIndexOf('/') + 1);
    }

    private APIUnitStatusConst getLatestUnitStatusFromMeta(OutputAudioMeta meta) {
        if (meta.getTtsDetail() != null) {
            return getLatestApiStatus(meta.getTtsDetail().getApiStatuses());
        } else if (meta.getVcDetail() != null) {
            return getLatestApiStatus(meta.getVcDetail().getApiStatuses());
        }
        return null;
    }

    private APIUnitStatusConst getLatestApiStatus(List<APIStatus> apiStatuses) {
        return apiStatuses.stream()
                .max(Comparator.comparing(APIStatus::getResponseAt)) // 가장 최신 APIStatus를 가져옴
                .map(APIStatus::getApiUnitStatusConst) // APIUnitStatusConst 추출
                .orElse(null); // 없을 경우 null 반환
    }

    // =========================  프로젝트 목록 ==========================


}