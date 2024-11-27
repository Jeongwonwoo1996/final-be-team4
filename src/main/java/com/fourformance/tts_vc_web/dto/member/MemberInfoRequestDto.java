package com.fourformance.tts_vc_web.dto.member;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoRequestDto {

    private String pwd; // 사용자가 입력한 비밀번호
}
