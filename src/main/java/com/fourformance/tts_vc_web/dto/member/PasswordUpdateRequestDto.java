package com.fourformance.tts_vc_web.dto.member;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordUpdateRequestDto {
    private String currentPassword; // 현재 비밀번호
    private String newPassword; // 새 비밀번호
    private String confirmPassword; // 새 비밀번호 확인
}
