package com.fourformance.tts_vc_web.service.member;

import com.fourformance.tts_vc_web.domain.entity.Member;
import com.fourformance.tts_vc_web.dto.member.*;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService_team_api1 {
    private final MemberRepository memberRepository;


    /**
     * 회원 로그인
     * @param requestDto 로그인 요청 데이터
     * @return 로그인 응답 데이터
     */
    public MemberLoginResponseDto login(MemberLoginRequestDto requestDto) {
        // 이메일로 회원 검색
        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 비밀번호 검증
        if (!member.getPwd().equals(requestDto.getPwd())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인 응답 DTO 생성
        return MemberLoginResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .build();
    }

    /**
     * 회원 정보 조회
     * @param email 세션에서 가져온 회원 이메일
     * @param pwd 사용자가 입력한 비밀번호
     * @return 회원 정보 응답 DTO
     */
    public MemberUpdateResponseDto getMemberInfo(String email, String pwd) {
        // 이메일로 회원 검색
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 비밀번호 검증
        if (!member.getPwd().equals(pwd)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 회원 정보 반환
        return MemberUpdateResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    /**
     * 회원 정보 수정
     * @param email 세션에서 얻은 회원 이메일
     * @param requestDto 수정할 회원 데이터 (이름, 전화번호)
     * @return 수정된 회원 정보 응답 DTO
     */
    public MemberUpdateResponseDto updateMemberInfo(String email, MemberUpdateRequestDto requestDto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 이름과 전화번호 수정
        member.updateMemberName(requestDto.getName());
        member.updateMemberPhoneNumber(requestDto.getPhoneNumber());
        memberRepository.save(member);

        return MemberUpdateResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    /**
     * 비밀번호 수정
     * @param email 세션에서 얻은 회원 이메일
     * @param requestDto 비밀번호 수정 요청 데이터
     */
    public void updatePassword(String email, PasswordUpdateRequestDto requestDto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 현재 비밀번호 검증
        if (!member.getPwd().equals(requestDto.getCurrentPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 확인
        if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 업데이트
        member.updateMember(requestDto.getNewPassword(), null, null);
        memberRepository.save(member);
    }



}
