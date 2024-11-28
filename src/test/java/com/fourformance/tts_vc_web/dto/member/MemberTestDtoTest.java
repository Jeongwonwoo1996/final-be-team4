package com.fourformance.tts_vc_web.dto.member;

import com.fourformance.tts_vc_web.domain.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberTestDtoTest {

    @Test
    public void 모델_매퍼_테스트() {
        Member member = Member.createMember(
                "email@email.com",
                "123",
                "네임",
                0,
                LocalDateTime.now(),
                "010123123",
                true);

        System.out.println("========= member = " + member);

        MemberTestDto testDto = MemberTestDto.createMemberDto(member); // 엔티티를 dto로
        System.out.println("=========  testDto = " + testDto);
        Member testEntity = testDto.createMember(); // dto를 엔티티로
        System.out.println("=========  testEntity = " + testEntity);

    }
}