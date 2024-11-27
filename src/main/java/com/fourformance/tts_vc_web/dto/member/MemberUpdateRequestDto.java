package com.fourformance.tts_vc_web.dto.member;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberUpdateRequestDto {
    /**
     * 회원 정보 수정 요청 DTO
     * 사용자가 수정할 정보를 서버로 전달할 때 사용됩니다.
     */

    private Long memberId; // 수정할 회원의 고유 ID
    private String pwd; // 수정할 비밀번호
    private String phoneNumber; // 수정할 연락처
    private String name; // 수정할 이름

}
