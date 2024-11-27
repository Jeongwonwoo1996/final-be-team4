package com.fourformance.tts_vc_web.service.member;



import com.fourformance.tts_vc_web.domain.entity.Member;

import com.fourformance.tts_vc_web.dto.member.*;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MemberService_jaehong {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */
    public MemberSignUpResponseDto signUp(MemberSignUpRequestDto requestDto) {
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 엔티티 생성
        Member member = Member.createMember(
                requestDto.getEmail(),
                requestDto.getPwd(),
                requestDto.getName(),
                null, // 성별 제거
                null, // 생년월일 제거
                requestDto.getPhoneNumber()
        );

        // 약관 동의 필드 설정
        member.restoreMember(); // 소프트 삭제 상태 복구 (초기 상태)

        // 데이터베이스 저장
        Member savedMember = memberRepository.save(member);

        // 응답 DTO 생성
        return MemberSignUpResponseDto.builder()
                .id(savedMember.getId())
                .email(savedMember.getEmail())
                .name(savedMember.getName())
                .createdAt(savedMember.getCreatedAt())
                .build();
    }

    /**
     * 이메일 중복 체크
     */
    public MemberIdCheckResponseDto checkEmailDuplicate(MemberIdCheckRequestDto requestDto) {
        boolean isDuplicate = memberRepository.existsByEmail(requestDto.getEmail());
        return MemberIdCheckResponseDto.builder()
                .isDuplicate(isDuplicate)
                .build();
    }

    /**
     * 회원 ID 찾기
     */
    public MemberIdFindResponseDto findId(MemberIdFindRequestDto requestDto) {
        // 이름과 전화번호로 회원 검색
        Optional<Member> member = memberRepository.findByNameAndPhoneNumber(
                requestDto.getName(),
                requestDto.getPhoneNumber()
        );

        if (member.isEmpty()) {
            throw new IllegalArgumentException("해당 정보를 가진 사용자를 찾을 수 없습니다.");
        }

        // 응답 DTO 생성
        return MemberIdFindResponseDto.builder()
                .email(member.get().getEmail())
                .build();
    }

    /**
     * 비밀번호 찾기
     */
    public MemberPasswordFindResponseDto findPassword(MemberPasswordFindRequestDto requestDto) {
        // 이메일로 회원 검색
        Optional<Member> member = memberRepository.findByEmail(requestDto.getEmail());

        if (member.isEmpty()) {
            throw new IllegalArgumentException("해당 이메일을 가진 사용자가 존재하지 않습니다.");
        }

        // 전화번호 검증
        if (!member.get().getPhoneNumber().equals(requestDto.getPhoneNumber())) {
            throw new IllegalArgumentException("전화번호가 일치하지 않습니다.");
        }

        // 응답 DTO 생성 (비밀번호 반환)
        return MemberPasswordFindResponseDto.builder()
                .email(member.get().getEmail())
                .password(member.get().getPwd()) // 비밀번호 그대로 반환
                .build();
    }
}