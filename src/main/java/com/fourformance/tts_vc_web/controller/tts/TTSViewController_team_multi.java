package com.fourformance.tts_vc_web.controller.tts;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.dto.common.DeleteReqDto;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.dto.tts.TTSDetailDto;
import com.fourformance.tts_vc_web.dto.tts.TTSProjectDto;
import com.fourformance.tts_vc_web.dto.tts.TTSProjectWithDetailsDto;
import com.fourformance.tts_vc_web.dto.tts.TTSSaveDto;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import com.fourformance.tts_vc_web.service.common.ProjectService_team_multi;
import com.fourformance.tts_vc_web.service.common.S3Service;
import com.fourformance.tts_vc_web.service.tts.TTSService_team_multi;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tts")
@RequiredArgsConstructor
public class TTSViewController_team_multi {

    private final TTSService_team_multi ttsService;
    private final ProjectService_team_multi projectService;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;


    // TTS 상태 로드 메서드
    @Operation(
            summary = "TTS 상태 로드",
            description = "TTS 프로젝트 상태를 가져옵니다.")
    @GetMapping("/{projectId}")
    public ResponseDto ttsLoad(@PathVariable("projectId") Long projectId) {

        // TTSProjectDTO와 TTSDetailDTO 리스트 가져오기
        TTSProjectDto ttsProjectDTO = ttsService.getTTSProjectDto(projectId);
        List<TTSDetailDto> ttsDetailsDTO = ttsService.getTTSDetailsDto(projectId);

        if (ttsProjectDTO == null) {
            throw new BusinessException(ErrorCode.NOT_EXISTS_PROJECT);
        }

        try {
            // DTO를 포함한 응답 객체 생성
            TTSProjectWithDetailsDto response = new TTSProjectWithDetailsDto(ttsProjectDTO, ttsDetailsDTO);
            return DataResponseDto.of(response);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }

    // TTS 상태 저장 메서드
    @Operation(
            summary = "TTS 상태 저장",
            description = "TTS 프로젝트 상태를 저장합니다.")
    @PostMapping("/save")
    public ResponseDto ttsSave(@RequestBody TTSSaveDto ttsSaveDto, HttpSession session) {
        try {
            // 임시 하드 코딩
            session.setAttribute("memberId", 1L);
            // 세션에 memberId 설정
            if (session.getAttribute("memberId") == null) {
                throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
            }

            Long memberId = (Long) session.getAttribute("memberId");

            Long projectId;
            if (ttsSaveDto.getProjectId() == null) {
                // projectId가 null인 경우, 새 프로젝트 생성
                projectId = ttsService.createNewProject(ttsSaveDto, memberId);
            } else {
                // projectId가 존재하면, 기존 프로젝트 업데이트
                projectId = ttsService.updateProject(ttsSaveDto, memberId);
            }
            // 상태 저장 후 결과 반환
            ResponseDto ttsLoadDto = ttsLoad(projectId);
            return DataResponseDto.of(ttsLoadDto, "TTS 프로젝트가 성공적으로 저장되었습니다.");

        } catch (BusinessException e) {
            throw e;  // 기존의 BusinessException 그대로 던짐
        }
//        catch (Exception e) {
//            throw new BusinessException(ErrorCode.SERVER_ERROR);  // 일반 예외를 서버 에러로 처리
//        }
    }

    // TTS 프로젝트 삭제
    @Operation(
            summary = "TTS 프로젝트 삭제",
            description = "TTS 프로젝트와 생성된 오디오를 전부 삭제합니다.")
    @DeleteMapping("/delete/{projectId}")
    public ResponseDto deleteTTSProject(@PathVariable("projectId") Long projectId) {

        // 타입 검증
        if (projectId == null) {
            throw new BusinessException(ErrorCode.INVALID_PROJECT_ID);
        }

        // 프로젝트 삭제
        projectService.deleteTTSProject(projectId);
        s3Service.deleteAudioPerProject(projectId); // 관련된 오디오 모두 버킷에서 삭제

        // 작업 상태 : Terminated(종료)
        return DataResponseDto.of("", "TTS 프로젝트가 정상적으로 삭제되었습니다.");
    }

    // TTS 선택된 모든 항목 삭제
    @Operation(
            summary = "TTS 선택된 항목 삭제",
            description = "TTS 프로젝트에서 선택된 모든 항목을 삭제합니다.")
    @DeleteMapping("/delete/details")
    public ResponseDto deleteTTSDetails(@RequestBody DeleteReqDto ttsDeleteDto) {

        // TTS 선택된 항목 삭제
        if (ttsDeleteDto.getDetailIds() != null) {
            projectService.deleteTTSDetail(ttsDeleteDto.getDetailIds());
        }

        // 선택된 오디오 삭제
        if (ttsDeleteDto.getAudioIds() != null) {
            projectService.deleteAudioIds(ttsDeleteDto.getAudioIds());
        }

        // 버킷에서 오디오 삭제
        for (Long metaId : ttsDeleteDto.getAudioIds()) {
            s3Service.deleteAudioOutput(metaId);
        }

        return DataResponseDto.of("", "선택된 모든 항목이 정상적으로 삭제되었습니다.");
    }

}
