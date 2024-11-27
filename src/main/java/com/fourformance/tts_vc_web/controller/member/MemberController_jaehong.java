package com.fourformance.tts_vc_web.controller.member;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.dto.member.*;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.service.member.MemberService_jaehong;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController_jaehong {

    private static final Logger LOGGER = Logger.getLogger(MemberController_jaehong.class.getName());
    private final MemberService_jaehong memberService;

    /**
     * 회원가입
     */
    @Operation(summary = "회원가입", description = "회원 정보를 기반으로 회원가입을 수행합니다.")
    @PostMapping("/signup")
    public ResponseDto signUp(@RequestBody MemberSignUpRequestDto requestDto) {
        try {
            MemberSignUpResponseDto responseDto = memberService.signUp(requestDto);
            LOGGER.info("회원가입 성공: " + responseDto.getEmail());
            return DataResponseDto.of(responseDto);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("회원가입 실패: " + e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA);
        } catch (Exception e) {
            LOGGER.severe("회원가입 처리 중 서버 오류 발생: " + e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 이메일 중복 체크
     */
    @Operation(summary = "이메일 중복 체크", description = "입력한 이메일이 중복되었는지 확인합니다.")
    @PostMapping("/check-id")
    public ResponseDto checkEmailDuplicate(@RequestBody MemberIdCheckRequestDto requestDto) {
        try {
            MemberIdCheckResponseDto responseDto = memberService.checkEmailDuplicate(requestDto);
            LOGGER.info("이메일 중복 체크 성공: " + requestDto.getEmail() + ", 중복 여부: " + responseDto.getIsDuplicate());
            return DataResponseDto.of(responseDto);
        } catch (Exception e) {
            LOGGER.severe("이메일 중복 체크 처리 중 오류 발생: " + e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 회원 ID 찾기
     */
    @Operation(summary = "회원 ID 찾기", description = "이름과 전화번호를 기반으로 회원의 이메일(ID)을 찾습니다.")
    @PostMapping("/find-id")
    public ResponseDto findId(@RequestBody MemberIdFindRequestDto requestDto) {
        try {
            MemberIdFindResponseDto responseDto = memberService.findId(requestDto);
            LOGGER.info("회원 ID 찾기 성공: 이름=" + requestDto.getName() + ", 이메일=" + responseDto.getEmail());
            return DataResponseDto.of(responseDto);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("회원 ID 찾기 실패: " + e.getMessage());
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        } catch (Exception e) {
            LOGGER.severe("회원 ID 찾기 처리 중 서버 오류 발생: " + e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 비밀번호 찾기
     */
    @Operation(summary = "비밀번호 찾기", description = "이메일과 전화번호를 기반으로 회원의 비밀번호를 반환합니다.")
    @PostMapping("/find-password")
    public ResponseDto findPassword(@RequestBody MemberPasswordFindRequestDto requestDto) {
        try {
            MemberPasswordFindResponseDto responseDto = memberService.findPassword(requestDto);
            LOGGER.info("비밀번호 찾기 성공: 이메일=" + requestDto.getEmail());
            return DataResponseDto.of(responseDto);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("비밀번호 찾기 실패: " + e.getMessage());
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        } catch (Exception e) {
            LOGGER.severe("비밀번호 찾기 처리 중 서버 오류 발생: " + e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
