package com.fourformance.tts_vc_web.controller.workspace;

import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.dto.workspace.ExportWithDownloadLinkDto;
import com.fourformance.tts_vc_web.dto.workspace.ProjectListDto;
import com.fourformance.tts_vc_web.dto.workspace.RecentExportDto;
import com.fourformance.tts_vc_web.dto.workspace.RecentProjectDto;
import com.fourformance.tts_vc_web.repository.OutputAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.ProjectRepository;
import com.fourformance.tts_vc_web.repository.workspace.OutputAudioMetaRepositoryCustomImpl;
import com.fourformance.tts_vc_web.service.common.ProjectService_team_aws;
import com.fourformance.tts_vc_web.service.common.S3Service;
import com.fourformance.tts_vc_web.service.workspace.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkSpaceController {

    private final WorkspaceService workspaceService;
    private final ProjectService_team_aws projectService;
    private final ProjectRepository projectRepository;
    private final OutputAudioMetaRepository outputAudioMetaRepository;
    private final OutputAudioMetaRepositoryCustomImpl outputAudioMetaRepositoryCustomImpl;
    private final S3Service s3Service;

    // 최근 5개의 프로젝트를 조회하는 api
    @Operation(summary = "최근 프로젝트 5개 조회", description = "해당 유저의 최근 프로젝트 5개를 조회합니다. <br>"
            + "유저의 id는 세션에서 가져옵니다. (회원 기능 구현 전 임시 하드코딩으로 멤버 id가 1인 유저의 최근 프로젝트 목록을 가져옵니다.")
    @GetMapping("/project-list")
    public ResponseDto getRecentProjects(HttpSession session) {

//        Long memberId = (Long) session.getAttribute("member_id");
        Long memberId = 1L; // 임시 하드코딩

        // Service에서 처리된 응답 그대로 반환
        List<RecentProjectDto> projects = workspaceService.getRecentProjects(memberId);
        return DataResponseDto.of(projects);
    }

    @GetMapping("/export-list")
    public ResponseDto getRecentExports(HttpSession session) {
        Long memberId = 1L; // 임시 하드코딩 (세션 구현 후 교체)
        List<RecentExportDto> exports = workspaceService.getRecentExports(memberId);
        return DataResponseDto.of(exports);
    }

    @GetMapping("/api/v1/projects")
    public ResponseEntity<List<ProjectListDto>> getProjects(
            @RequestParam(name = "keyword", required = false) String keyword,
            HttpSession session) {
//        Long memberId = (Long) session.getAttribute("memberId");
        Long memberId = 1L; // 개발단계 임시 하드코딩
        List<ProjectListDto> projects = projectRepository.findProjectsBySearchCriteria(memberId, keyword);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/projects")
    public ResponseDto getProjects(
            @RequestParam(name = "keyword", required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable,
            HttpSession session
    ) {
        //        Long memberId = (Long) session.getAttribute("memberId");
        Long memberId = 1L; // 개발단계 임시 하드코딩

        Page<ProjectListDto> projects = projectRepository.findProjectsBySearchCriteria(memberId, keyword, pageable);
        return DataResponseDto.of(projects);
    }

    @GetMapping("/api/v1/exports-test")
    public ResponseDto getExports(
            @RequestParam(name = "keyword", required = false) String keyword,
            HttpSession session
    ) {
        // 임시로 memberId를 하드코딩 (개발 단계)
        // 실제 배포에서는 세션에서 memberId를 가져와야 합니다.
        Long memberId = 1L; // (Long) session.getAttribute("memberId");

        // WorkspaceService의 메서드 호출
        List<ExportWithDownloadLinkDto> exports = workspaceService.getRecentExportsWithDownloadLink(memberId, keyword);

        // 결과를 ResponseDto로 래핑하여 반환
        return DataResponseDto.of(exports);
    }

    @GetMapping("/exports")
    public ResponseDto getExports2(
            @RequestParam(name = "keyword", required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable,
            HttpSession session

    ) {
        Long memberId = 1L;

        Page<ExportWithDownloadLinkDto> exports = outputAudioMetaRepository.findExportHistoryBySearchCriteria(memberId,
                keyword, pageable);
        return DataResponseDto.of(exports);
    }

    @DeleteMapping("/delete/project")
    public ResponseDto deleteProjects(@RequestBody List<Long> projectIds) {
        // 서비스 호출 및 삭제 로직 수행
        for (Long projectId : projectIds) {
            s3Service.deleteAudioPerProject(projectId); // 연관된 모든 객체 isDeleted 처리와 버킷 오디오 삭제까지 다 함.
        }

        // 상태 코드 반환
        return DataResponseDto.of("삭제가 완료되었습니다.");
    }

    @DeleteMapping("/delete/export")
    public ResponseDto deleteExports(@RequestBody List<Long> audioId) {
        for (Long exportId : audioId) {
            s3Service.deleteAudioOutput(exportId);
        }

        return DataResponseDto.of("삭제가 완료되었습니다.");
    }
}