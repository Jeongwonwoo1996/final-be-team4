package com.fourformance.tts_vc_web.service.workspace;

import static com.fourformance.tts_vc_web.domain.entity.QConcatDetail.concatDetail;
import static com.fourformance.tts_vc_web.domain.entity.QTTSDetail.tTSDetail;
import static com.fourformance.tts_vc_web.domain.entity.QVCDetail.vCDetail;

import com.fourformance.tts_vc_web.common.constant.APIStatusConst;
import com.fourformance.tts_vc_web.common.constant.APIUnitStatusConst;
import com.fourformance.tts_vc_web.common.constant.ProjectType;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.APIStatus;
import com.fourformance.tts_vc_web.domain.entity.ConcatProject;
import com.fourformance.tts_vc_web.domain.entity.OutputAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.Project;
import com.fourformance.tts_vc_web.domain.entity.TTSProject;
import com.fourformance.tts_vc_web.domain.entity.VCProject;
import com.fourformance.tts_vc_web.dto.workspace.ExportListDto;
import com.fourformance.tts_vc_web.dto.workspace.ExportWithDownloadLinkDto;
import com.fourformance.tts_vc_web.dto.workspace.RecentExportDto;
import com.fourformance.tts_vc_web.dto.workspace.RecentProjectDto;
import com.fourformance.tts_vc_web.repository.OutputAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.ProjectRepository;
import com.fourformance.tts_vc_web.repository.workspace.OutputAudioMetaRepositoryCustomImpl;
import com.fourformance.tts_vc_web.service.common.S3Service;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
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
    private final OutputAudioMetaRepositoryCustomImpl outputAudioMetaRepositoryCustomImpl;
    private final JPAQueryFactory queryFactory;

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
            String script = null;

            // API 상태 및 스크립트를 TTSProject, VCProject, ConcatProject에 따라 가져옴
            if (project instanceof TTSProject tts) {
                apiStatus = tts.getApiStatus();
                script = queryFactory
                        .select(tTSDetail.unitScript)
                        .from(tTSDetail)
                        .where(tTSDetail.ttsProject.id.eq(tts.getId())
                                .and(tTSDetail.isDeleted.isFalse()))
                        .orderBy(tTSDetail.unitSequence.asc())
                        .fetchFirst();
            } else if (project instanceof VCProject vc) {
                apiStatus = vc.getApiStatus();
                script = queryFactory
                        .select(vCDetail.unitScript)
                        .from(vCDetail)
                        .where(vCDetail.vcProject.id.eq(vc.getId())
                                .and(vCDetail.isDeleted.isFalse()))
                        .orderBy(vCDetail.createdAt.asc())
                        .fetchFirst();
            } else if (project instanceof ConcatProject concat) {
                script = queryFactory
                        .select(concatDetail.unitScript)
                        .from(concatDetail)
                        .where(concatDetail.concatProject.id.eq(concat.getId())
                                .and(concatDetail.isDeleted.isFalse()))
                        .orderBy(concatDetail.audioSeq.asc())
                        .fetchFirst();
            }

            return new RecentProjectDto(
                    project.getId(),
                    type,
                    project.getProjectName(),
                    apiStatus, // 상태는 TTSProject와 VCProject만 포함, 나머지는 null
                    script, // 스크립트 추가
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
        RecentExportDto dto = new RecentExportDto(); // DTO를 생성

        // 공통 설정
        dto.setMetaId(meta.getId()); // OutputAudioMeta ID
        dto.setFileName(extractFileName(meta.getBucketRoute())); // 파일명 추출
        dto.setUrl(s3Service.generatePresignedUrl(meta.getBucketRoute())); // S3 Presigned URL 생성
        dto.setUnitStatus(getLatestUnitStatusFromMeta(meta)); // 최신 Unit Status 설정

        if (meta.getTtsDetail() != null) {
            // TTS 프로젝트 관련 설정
            TTSProject ttsProject = meta.getTtsDetail().getTtsProject();
            dto.setProjectId(ttsProject.getId()); // TTS 프로젝트 ID
            dto.setProjectName(ttsProject.getProjectName());
            dto.setProjectType(ProjectType.TTS);
            dto.setScript(meta.getTtsDetail().getUnitScript());

        } else if (meta.getVcDetail() != null) {
            // VC 프로젝트 관련 설정
            VCProject vcProject = meta.getVcDetail().getVcProject();
            dto.setProjectId(vcProject.getId()); // VC 프로젝트 ID
            dto.setProjectName(vcProject.getProjectName());
            dto.setProjectType(ProjectType.VC);
            dto.setScript(meta.getVcDetail().getUnitScript());

        } else if (meta.getConcatProject() != null) {
            // Concat 프로젝트 관련 설정
            ConcatProject concatProject = meta.getConcatProject();
            dto.setProjectId(concatProject.getId()); // Concat 프로젝트 ID
            dto.setProjectName(concatProject.getProjectName());
            List<String> scripts = outputAudioMetaRepository.findConcatDetailScriptsByOutputAudioMetaId(meta.getId());
            String combinedScripts = String.join(" ", scripts); // 여러 스크립트를 공백으로 연결
            dto.setScript(combinedScripts);
            dto.setProjectType(ProjectType.CONCAT);
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

    // sojeong 임시 메서드 ==========================시작===============================

    /**
     * ExportListDto 리스트를 ExportWithDownloadLinkDto 리스트로 변환하는 매핑 메서드
     */
    private ExportWithDownloadLinkDto mapToExportWithDownloadLinkDto(ExportListDto export) {
        ExportWithDownloadLinkDto dto = new ExportWithDownloadLinkDto();

        // 공통 설정
        dto.setMetaId(export.getOutputAudioMetaId());
        dto.setFileName(export.getFileName());
        dto.setUnitStatus(export.getUnitStatus());
        dto.setCreatedAt(export.getCreateAt());

        // Presigned URL 설정
        String bucketRoute = export.getBucketRoute(); // audioUrl이 bucketRoute임을 확인
        String presignedUrl = s3Service.generatePresignedUrl(bucketRoute);
        dto.setDownloadLink(presignedUrl);

        // 프로젝트 타입에 따른 설정
        switch (export.getProjectType().toUpperCase()) {
            case "TTS":
                dto.setProjectType("TTS");
                dto.setProjectName(export.getProjectName());
                dto.setScript(export.getScript());
                break;
            case "VC":
                dto.setProjectType("VC");
                dto.setProjectName(export.getProjectName());
                dto.setScript(export.getScript());
                break;
            case "CONCAT":
                dto.setProjectType("CONCAT");
                dto.setProjectName(export.getProjectName());
                dto.setScript(export.getScript());
                break;
            default:
                dto.setProjectType(null);
                dto.setProjectName("Unknown Project");
                dto.setScript("Unknown Script");
        }

        return dto;
    }

    /**
     * 최신 Export 작업 내역 조회 및 변환
     */
    public List<ExportWithDownloadLinkDto> getRecentExportsWithDownloadLink(Long memberId, String keyword) {
        if (memberId == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // Repository에서 ExportListDto 리스트 조회
        List<ExportListDto> exportList = outputAudioMetaRepositoryCustomImpl.findExportHistoryBySearchCriteria(memberId,
                keyword);

        // ExportListDto 리스트를 ExportWithDownloadLinkDto 리스트로 변환
        List<ExportWithDownloadLinkDto> exportWithLinks = new ArrayList<>();
        for (ExportListDto export : exportList) {
            ExportWithDownloadLinkDto dto = mapToExportWithDownloadLinkDto(export);
            exportWithLinks.add(dto);
        }

        return exportWithLinks;
    }

//    public Page<ExportWithDownloadLinkDto> getRecentExportsWithDownloadLink(Long memberId, String keyword, Pageable pageable) {
//        if (memberId == null) {
//            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
//        }
//
//        // Repository에서 Page<ExportListDto> 조회
//        Page<ExportListDto> exportListPage = outputAudioMetaRepositoryCustomImpl.findExportHistoryBySearchCriteria(memberId, keyword, pageable);
//
//        // ExportListDto -> ExportWithDownloadLinkDto 변환
//        List<ExportWithDownloadLinkDto> exportWithLinks = exportListPage.getContent().stream()
//                .map(this::mapToExportWithDownloadLinkDto)
//                .collect(Collectors.toList());
//
//        // Page<ExportWithDownloadLinkDto>로 반환
//        return new PageImpl<>(exportWithLinks, pageable, exportListPage.getTotalElements());
//    }
    // sojeong 임시 메서드 ==========================끝===============================
}
