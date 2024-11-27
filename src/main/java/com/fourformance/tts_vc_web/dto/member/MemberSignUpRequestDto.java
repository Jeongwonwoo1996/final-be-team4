package com.fourformance.tts_vc_web.dto.member;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSignUpRequestDto {
    /**
     * 회원가입 요청 DTO
     * 사용자가 입력한 회원 정보를 담아 서버로 전달합니다.
     */

    private String email;       // 이메일
    private String pwd;         // 비밀번호
    private String pwdConfirm;  // 비밀번호 확인
    private String name;        // 이름
    private String phoneNumber; // 전화번호
    private boolean tou;        // 약관 동의 여부
}
