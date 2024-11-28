package com.fourformance.tts_vc_web.dto.member;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberUpdateResponseDto {
    /**
     * 회원 정보 수정 응답 DTO
     * 수정된 정보를 서버가 클라이언트로 전달합니다.
     */

    private Long id; // 수정된 회원의 고유 ID
    private String email; // 수정된 회원의 이메일
    private String name; // 수정된 회원의 이름
    private String phoneNumber; // 수정된 회원의 전화번호
    private LocalDateTime updatedAt; // 정보가 수정된 시간
}
