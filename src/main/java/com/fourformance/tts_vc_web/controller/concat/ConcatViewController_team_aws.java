package com.fourformance.tts_vc_web.controller.concat;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.Member;
import com.fourformance.tts_vc_web.dto.common.DeleteReqDto;
import com.fourformance.tts_vc_web.dto.concat.ConcatSaveDto;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import com.fourformance.tts_vc_web.service.common.ProjectService_team_aws;
import com.fourformance.tts_vc_web.service.common.S3Service;
import com.fourformance.tts_vc_web.service.concat.ConcatService_team_aws;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concat")
public class ConcatViewController_team_aws {

    private final ConcatService_team_aws concatService;
    private final ProjectService_team_aws projectService;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;


    // Concat 상태 저장 메서드
    @Operation(
            summary = "Concat 상태 저장",
            description = "Concat 프로젝트 상태를 저장합니다.")
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto concatSave(
            @RequestPart(value = "concatSaveDto") ConcatSaveDto concatSaveDto, // 반드시 "concatSaveDto" 이름 지정
            @RequestPart(value = "file", required = false) List<MultipartFile> files,
            HttpSession session) {

        if (session.getAttribute("memberId") == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        Long memberId = (Long) session.getAttribute("memberId");

        // Member 객체 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Long projectId = concatService.saveConcatProject(concatSaveDto, files, member);

        return DataResponseDto.of(projectId, "Concat 상태가 성공적으로 저장되었습니다.");
    }


    // Concat 프로젝트 삭제
    @Operation(
            summary = "Concat 프로젝트 삭제",
            description = "해당 Concat 프로젝트를 삭제하고 관련된 소스, 아웃풋 오디오를 삭제합니다.")
    @PostMapping("/delete/{projectId}")
    public ResponseDto deleteConcatProject(@PathVariable("projectId") Long projectId) {

        // 타입 검증
        if (projectId == null) {
            throw new BusinessException(ErrorCode.INVALID_PROJECT_ID);
        }

        // 프로젝트 삭제
        projectService.deleteProject(projectId);
        s3Service.deleteAudioPerProject(projectId);

        // 작업 상태 : Terminated (종료)
        return DataResponseDto.of("", "Concat 프로젝트가 정상적으로 삭제되었습니다.");
    }

    // Concat 선택된 모든 유닛 삭제
    @Operation(
            summary = "Concat 선택된 항목 삭제",
            description = "Concat 프로젝트에서 선택된 모든 항목을 삭제합니다.")
    @PostMapping("/delete/details")
    public ResponseDto deleteConcatDetails(@RequestBody DeleteReqDto deleteDto) {

        // Concat 선택된 항목 삭제
        if (deleteDto.getDetailIds() != null) {
            projectService.deleteSelectedDetails(deleteDto.getDetailIds());
        }
        if (deleteDto.getAudioIds() != null) {
            projectService.deleteAudioIds(deleteDto.getAudioIds());

            // 버킷에서 오디오 파일 삭제
            for (Long audioId : deleteDto.getAudioIds()) {
                s3Service.deleteAudioMember(audioId);
            }
        }

        return DataResponseDto.of("", "선택된 모든 항목이 정상적으로 삭제되었습니다.");
    }
}
