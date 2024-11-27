package com.fourformance.tts_vc_web.dto.member;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberPasswordFindResponseDto {
    /**
     * 비밀번호 찾기 응답 DTO
     * 서버가 임시 비밀번호를 생성해 사용자에게 전달합니다.
     */

    private String email; // 비밀번호가 변경된 회원의 이메일
    private String password; // 서버에서 생성된 임시 비밀번호 (사용자가 이후 변경해야 함)
}
