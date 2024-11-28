package com.fourformance.tts_vc_web.service.member;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
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
     * @param requestDto 회원가입 요청 데이터
     * @return 회원가입 응답 데이터
     */
    public MemberSignUpResponseDto signUp(MemberSignUpRequestDto requestDto) {
        // 약관 동의 검증
        if (Boolean.FALSE.equals(requestDto.getTou())) { // 약관 미동의
            throw new BusinessException(ErrorCode.TOU_NOT_AGREED);
        }

        // 이메일 중복 체크
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new BusinessException(ErrorCode.SIGNUP_DUPLICATE_EMAIL);
        }

        // 비밀번호 일치 여부 확인
        if (!requestDto.getPwd().equals(requestDto.getPwdConfirm())) {
            throw new BusinessException(ErrorCode.SIGNUP_PASSWORD_MISMATCH);
        }

        // 엔티티 생성 (팩토리 메서드 사용)
        Member member = Member.createMember(
                requestDto.getEmail(),
                requestDto.getPwd(),
                requestDto.getName(),
                null, // 성별 제거
                null, // 생년월일 제거
                requestDto.getPhoneNumber(),
                requestDto.getTou() // 약관 동의 값 전달
        );

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
     *
     * @param requestDto 이메일 중복 체크 요청 데이터
     * @return 이메일 중복 여부 응답 데이터
     */
    public MemberIdCheckResponseDto checkEmailDuplicate(MemberIdCheckRequestDto requestDto) {
        // 이메일 검증
        if (requestDto.getEmail() == null || requestDto.getEmail().isEmpty()) {
            throw new BusinessException(ErrorCode.EMAIL_MISSING);
        }

        // 이메일 형식 검증
        if (!requestDto.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_FORMAT);
        }

        // 이메일 중복 여부 확인
        boolean isDuplicate = memberRepository.existsByEmail(requestDto.getEmail());
        return MemberIdCheckResponseDto.builder()
                .isDuplicate(isDuplicate) // 중복 여부 반환
                .build();
    }

        /**
         * 회원 이메일(ID) 찾기
         *
         * @param requestDto 이름과 전화번호를 포함한 요청 데이터
         * @return 회원 이메일(ID) 응답 데이터
         */
        public MemberIdFindResponseDto findId(MemberIdFindRequestDto requestDto) {
            // 이름 검증
            if (requestDto.getName() == null || requestDto.getName().isEmpty()) {
                throw new BusinessException(ErrorCode.NAME_MISSING);
            }

            // 전화번호 검증
            if (requestDto.getPhoneNumber() == null || requestDto.getPhoneNumber().isEmpty()) {
                throw new BusinessException(ErrorCode.PHONE_NUMBER_MISSING);
            }

            // 전화번호 형식 검증
            if (!requestDto.getPhoneNumber().matches("^\\d{2,3}-\\d{3,4}-\\d{4}$")) {
                throw new BusinessException(ErrorCode.INVALID_PHONE_NUMBER_FORMAT);
            }

            // 이름과 전화번호로 회원 검색
            Optional<Member> memberOptional = memberRepository.findByNameAndPhoneNumber(
                    requestDto.getName(),
                    requestDto.getPhoneNumber()
            );

            if (memberOptional.isEmpty()) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }

            // 이메일 반환
            Member member = memberOptional.get();
            return MemberIdFindResponseDto.builder()
                    .email(member.getEmail()) // 회원 이메일 반환
                    .build();
        }



    /**
     * 비밀번호 찾기
     *
     * @param requestDto 비밀번호 찾기 요청 데이터
     * @return 비밀번호 찾기 응답 데이터
     */
    public MemberPasswordFindResponseDto findPassword(MemberPasswordFindRequestDto requestDto) {
        // 이메일 검증
        if (requestDto.getEmail() == null || requestDto.getEmail().isEmpty()) {
            throw new BusinessException(ErrorCode.EMAIL_MISSING);
        }

        // 전화번호 검증
        if (requestDto.getPhoneNumber() == null || requestDto.getPhoneNumber().isEmpty()) {
            throw new BusinessException(ErrorCode.PHONE_NUMBER_MISSING);
        }

        // 이메일 형식 검증
        if (!requestDto.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_FORMAT);
        }

        // 전화번호 형식 검증
        if (!requestDto.getPhoneNumber().matches("^\\d{2,3}-\\d{3,4}-\\d{4}$")) {
            throw new BusinessException(ErrorCode.INVALID_PHONE_NUMBER_FORMAT);
        }

        // 이메일로 회원 검색
        Optional<Member> memberOptional = memberRepository.findByEmail(requestDto.getEmail());
        if (memberOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Member member = memberOptional.get();

        // 전화번호 매칭 검증
        if (!member.getPhoneNumber().equals(requestDto.getPhoneNumber())) {
            throw new BusinessException(ErrorCode.EMAIL_PHONE_NUMBER_MISMATCH);
        }

        // 비밀번호 반환
        return MemberPasswordFindResponseDto.builder()
                .email(member.getEmail())
                .password(member.getPwd()) // 회원 비밀번호 반환
                .build();
    }
}
