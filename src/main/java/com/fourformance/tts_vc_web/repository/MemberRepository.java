package com.fourformance.tts_vc_web.repository;

import com.fourformance.tts_vc_web.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {


    /*
     * 이메일로 사용자 찾기
     * @param email 회원의 이메일
     * @return 이메일에 해당하는 회원(Optional)
     */
    Optional<Member> findByEmail(String email);

    /**
     * 이름과 전화번호로 사용자 찾기
     * @param name 회원 이름
     * @param phoneNumber 회원 전화번호
     * @return 이름과 전화번호에 해당하는 회원(Optional)
     */
    Optional<Member> findByNameAndPhoneNumber(String name, String phoneNumber);

    /**
     * 이메일 중복 체크
     * @param email 회원 이메일
     * @return 이메일이 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByEmail(String email);

}
