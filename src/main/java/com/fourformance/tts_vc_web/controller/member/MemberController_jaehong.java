package com.fourformance.tts_vc_web.controller.member;

import com.fourformance.tts_vc_web.dto.member.*;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.service.member.MemberService_jaehong;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member Controller", description = "회원 관리와 관련된 작업을 제공합니다.")
public class MemberController_jaehong {

    private final MemberService_jaehong memberService;

    /**
     * 회원가입
     */
    @Operation(summary = "회원가입", description = "회원 정보를 기반으로 회원가입을 수행합니다.")
    @PostMapping("/signup")
    public ResponseDto signUp(@RequestBody MemberSignUpRequestDto requestDto) {
        MemberSignUpResponseDto responseDto = memberService.signUp(requestDto);
        return DataResponseDto.of(responseDto);
    }

    /**
     * 이메일 중복 체크
     */
    @Operation(summary = "이메일 중복 체크", description = "입력한 이메일이 중복되었는지 확인합니다.")
    @PostMapping("/check-id")
    public ResponseDto checkEmailDuplicate(@RequestBody MemberIdCheckRequestDto requestDto) {
        MemberIdCheckResponseDto responseDto = memberService.checkEmailDuplicate(requestDto);
        return DataResponseDto.of(responseDto);
    }

    /**
     * 회원 ID 찾기 (이메일)
     */
    @Operation(summary = "회원 ID 찾기", description = "이름과 전화번호를 기반으로 회원 이메일(ID)을 찾습니다.")
    @PostMapping("/find-id")
    public ResponseDto findId(@RequestBody MemberIdFindRequestDto requestDto) {
        MemberIdFindResponseDto responseDto = memberService.findId(requestDto);
        return DataResponseDto.of(responseDto);
    }

    /**
     * 비밀번호 찾기
     */
    @Operation(summary = "비밀번호 찾기", description = "이메일과 전화번호를 기반으로 회원의 비밀번호를 반환합니다.")
    @PostMapping("/find-password")
    public ResponseDto findPassword(@RequestBody MemberPasswordFindRequestDto requestDto) {
        MemberPasswordFindResponseDto responseDto = memberService.findPassword(requestDto);
        return DataResponseDto.of(responseDto);
    }
}
