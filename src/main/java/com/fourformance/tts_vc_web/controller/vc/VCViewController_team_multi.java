package com.fourformance.tts_vc_web.controller.vc;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.Member;
import com.fourformance.tts_vc_web.dto.common.DeleteReqDto;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.dto.vc.VCProjectResDto;
import com.fourformance.tts_vc_web.dto.vc.VCProjectWithDetailResDto;
import com.fourformance.tts_vc_web.dto.vc.VCSaveDto;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import com.fourformance.tts_vc_web.repository.VCProjectRepository;
import com.fourformance.tts_vc_web.dto.vc.*;
import com.fourformance.tts_vc_web.service.common.ProjectService_team_multi;
import com.fourformance.tts_vc_web.service.vc.VCService_team_multi;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@RequestMapping("/vc")
@RequiredArgsConstructor
public class VCViewController_team_multi {

    private final ProjectService_team_multi projectService;
    private final VCService_team_multi vcService;
    private final VCProjectRepository vcProjectRepository;
    private final MemberRepository memberRepository;


    // VC Trg 오디오 불러오기
    @Operation(
            summary = "VC TRG 오디오 리스트 로드",
            description = "VC TRG 오디오 리스트를 가져옵니다." )
    @GetMapping("/trg-audio")
    public ResponseDto trgAudioLoad(HttpSession session) {
        Long memberId = (Long) session.getAttribute("member_id");
        memberId = 1L;
        List<TrgAudioDto> trgAudioDtoList;

        if (memberId != null) {
            trgAudioDtoList = vcService.getTrgAudioList(memberId);
        } else {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        return DataResponseDto.of(trgAudioDtoList, "Trg 오디오 리스트가 성공적으로 반환되었습니다.");
    }


        // VC 상태 로드 메서드
    @Operation(
            summary = "VC 상태 로드",
            description = "VC 프로젝트 상태를 가져옵니다." )
    @GetMapping("/{projectId}")
    public ResponseDto vcLoad(@PathVariable("projectId") Long projectId) {

        // VCProjectDTO와 VCDetailDTO 리스트 가져오기
        VCProjectResDto vcProjectDTO = vcService.getVCProjectDto(projectId);
        List<VCDetailLoadDto> vcDetailsDTO = vcService.getVCDetailsDto(projectId);

        if (vcProjectDTO == null) {
            throw new BusinessException(ErrorCode.NOT_EXISTS_PROJECT);
        }

        try {
            // DTO를 포함한 응답 객체 생성
            VCProjectWithDetailResDto response = new VCProjectWithDetailResDto(vcProjectDTO, vcDetailsDTO);
            return DataResponseDto.of(response);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }

    }

    /**
     * VC 상태 저장 메서드
     * - 파일과 JSON 데이터를 함께 처리
     */
    @Operation(
            summary = "VC 상태 저장",
            description = "VC 프로젝트 상태를 저장합니다." +
                    "<br>- 새로운 프로젝트를 생성하거나 기존 프로젝트를 업데이트합니다." +
                    "<br>- 사용자가 s3에 업로드한 오디오를 선택하면 MultipartFile의 값은 null로 보냅니다." +
                    "<br>- 파일(MultipartFile)과 메타데이터(JSON)를 동시에 전송해야 합니다." )
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RedirectView saveVCProject(
            @RequestPart(value = "file", required = false) List<MultipartFile> files,
            @RequestPart("metadata") VCSaveDto vcSaveDto, HttpSession session) {
        // 세션에 임의의 memberId 설정
        if (session.getAttribute("memberId") == null) {
            session.setAttribute("memberId", 1L);
        }

        Long memberId = (Long) session.getAttribute("memberId");

        // Member 객체 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("Member not found"));

        // Member 객체를 서비스에 전달
        Long projectId = vcService.saveVCProject(vcSaveDto, files, member);


        return new RedirectView("/vc/" + projectId); // TTS 상태 로드 URL로 리다이렉트
    }


    // VC 프로젝트 삭제
    @Operation(
            summary = "VC 프로젝트 삭제",
            description = "VC 프로젝트와 생성된 오디오 등 관련된 데이터를 전부 삭제합니다." )
    @DeleteMapping("/delete/{projectId}")
    public ResponseDto deleteVCProject(@PathVariable("projectId") Long projectId) {
        // 타입 검증
        if(projectId == null) { throw new BusinessException(ErrorCode.INVALID_PROJECT_ID); }

        // 프로젝트 삭제
        projectService.deleteVCProject(projectId);

        // 작업 상태 : Terminated(종료) - 코드 추가 예정


        return DataResponseDto.of("","VC 프로젝트가 정상적으로 삭제되었습니다.");
    }


    // VC 선택된 모든 항목 삭제
    @Operation(
            summary = "VC 선택된 항목 삭제",
            description = "VC 프로젝트에서 선택된 모든 항목을 삭제합니다." )
    @DeleteMapping("/delete/details")
    public ResponseDto deleteVCDetail(@RequestBody DeleteReqDto vcDeleteDto) {

        // VC 선택된 항목 삭제
        if(vcDeleteDto.getDetailIds() != null) {  projectService.deleteVCDetail(vcDeleteDto.getDetailIds()); }

        // 선택된 오디오 삭제
        if(vcDeleteDto.getAudioIds() != null ) {  projectService.deleteAudioIds(vcDeleteDto.getAudioIds());}

        // 작업 상태 : Terminated(종료) - 코드 추가 예정

        return DataResponseDto.of("","선택된 모든 항목이 정상적으로 삭제되었습니다.");
    }

    // TRG 오디오 선택된 모든 항목 삭제
    @Operation(
            summary = "VC 프로젝트 target 오디오 선택 항목 삭제",
            description = "VC 프로젝트에서 target 오디오 선택된 모든 항목을 삭제합니다." )
    @DeleteMapping("/delete/target/{audioId}")
    public ResponseDto deleteTRGAudio(@PathVariable("audioId") Long targetAudioId) {

        // 타겟 오디오 삭제
        vcService.deleteTRGAudio(targetAudioId);

        return DataResponseDto.of("","Target 오디오가 정상적으로 삭제되었습니다.");
    }
}
