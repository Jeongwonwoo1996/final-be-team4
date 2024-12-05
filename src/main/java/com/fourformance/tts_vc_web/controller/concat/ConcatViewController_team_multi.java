package com.fourformance.tts_vc_web.controller.concat;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.Member;
import com.fourformance.tts_vc_web.dto.concat.CNCTDetailDto;
import com.fourformance.tts_vc_web.dto.concat.CNCTProjectDto;
import com.fourformance.tts_vc_web.dto.concat.CNCTProjectWithDetailDto;
import com.fourformance.tts_vc_web.dto.concat.ConcatSaveDto;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import com.fourformance.tts_vc_web.service.concat.ConcatService_team_aws;
import com.fourformance.tts_vc_web.service.concat.ConcatService_team_multi;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/concat")
@RequiredArgsConstructor
public class ConcatViewController_team_multi {

    private final ConcatService_team_multi concatService;
    private final MemberRepository memberRepository;
    private final ConcatService_team_aws concatServiceAws;


    // Concat 상태 로드 메서드
    @Operation(
            summary = "Concat 상태 로드",
            description = "Concat 프로젝트 상태를 가져옵니다." )
    @GetMapping("/{projectId}")
    public ResponseDto concatLoad(@PathVariable("projectId") Long projectId) {

        // CNCTProjectDTO와 CNCTDetailDTO 리스트 가져오기
        CNCTProjectDto cnctProjectDTO = concatService.getConcatProjectDto(projectId);
        List<CNCTDetailDto> cnctDetailsDTO = concatService.getConcatDetailsDto(projectId);

            if (cnctProjectDTO == null) {
                throw new BusinessException(ErrorCode.NOT_EXISTS_PROJECT);
            }

            try {
                // DTO를 포함한 응답 객체 생성
                CNCTProjectWithDetailDto response = new CNCTProjectWithDetailDto(cnctProjectDTO, cnctDetailsDTO);
                return DataResponseDto.of(response);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }

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
                .orElseThrow(() -> new IllegalStateException("Member not found"));

        Long projectId = concatServiceAws.saveConcatProject(concatSaveDto, files, member);

        ResponseDto concatLoadDto = concatLoad(projectId);
        return DataResponseDto.of(concatLoadDto, "Concat 프로젝트가 성공적으로 저장되었습니다.");
    }

}
