package com.fourformance.tts_vc_web.controller.common;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.Member;
import com.fourformance.tts_vc_web.dto.member.MemberTestDto;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class TestController {

    // 성공 케이스 API
    @GetMapping("/test/success")
    public ResponseDto testSuccess() {
        Member member = Member.createMember("abc@abc.com","pwd123:","철수",0, LocalDateTime.now(),"010123123",true);
        MemberTestDto memberDto = MemberTestDto.createMemberDto(member);
        return DataResponseDto.of(memberDto);  // DataResponseDto 사용
    }

    // 실패 케이스 API (에러 파라미터에 따라 예외 발생)
    @GetMapping("/test/fail")
    public ResponseDto testFail(@RequestParam("Do Would you like to throw an exception?") String error) {
        if ("yes".equals(error)) {
            throw new BusinessException(ErrorCode.NOT_EXISTS_AUDIO);  // 예외 발생
        }
        // 성공 케이스로 반환
        return DataResponseDto.of("성공: 예외가 발생하지 않았습니다.");
    }
}