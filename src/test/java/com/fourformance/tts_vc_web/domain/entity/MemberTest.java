package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void 멤버_생성_테스트() {

        // given
            // 멤버 객체 생성
        Member beforeMember = Member.createMember("email@email.com","pwd123","name123",0, LocalDateTime.now(),"01010101100",true);
            // 멤버 객체 DB에 저장
        memberRepository.save(beforeMember);
        em.flush();
        em.clear();

        // when
            // DB에 저장한 멤버 객체 조회
        Member afterMember = memberRepository.findById(beforeMember.getId()).get();

        // then
            // 두 객체의 id 비교
        assertEquals(beforeMember.getId(), afterMember.getId());
            // 두 객체의 이메일 비교
        assertEquals(beforeMember.getEmail(), afterMember.getEmail());

    }

    @Test
    public void 멤버_업데이트_테스트() {

        // given
            // 멤버 객체 생성
        Member beforeMember = Member.createMember("email@email.com","pwd123","name123",0, LocalDateTime.now(),"01010101100",true);
            // 멤버 객체 DB에 저장
        memberRepository.save(beforeMember);
        em.flush();
        em.clear();
            // DB에 저장한 멤버 객체 조회
        Member afterMember = memberRepository.findById(beforeMember.getId()).get();
            // 제대로 조회했는지 검증
        assertEquals(beforeMember.getId(), afterMember.getId());

        // when
            // 조회한 객체를 업데이트
        afterMember.updateMember("updatedPWD","010123",LocalDateTime.now());
            // 업데이트한 객체를 DB에 저장
        memberRepository.save(afterMember);
        em.flush();
        em.clear();
            // 업데이트후 DB에 저장한 객체를 다시 조회
        Member afterUpdateMember = memberRepository.findById(afterMember.getId()).get();

        // then
            // 업데이트 전과 후 pwd 비교 -> 달라야 성공
        assertNotEquals(beforeMember.getPwd(), afterUpdateMember.getPwd());
            // 업데이트 후 pwd 검증
        assertEquals("updatedPWD", afterMember.getPwd());

    }

    @Test
    public void 멤버_업데이트_실패_테스트() {
        // given
        // 멤버 객체 생성
        Member beforeMember = Member.createMember("email@email.com","pwd123","name123",0, LocalDateTime.now(),"01010101100",true);
        // 멤버 객체 DB에 저장
        memberRepository.save(beforeMember);
        em.flush();
        em.clear();
        // DB에 저장한 멤버 객체 조회
        Member afterMember = memberRepository.findById(beforeMember.getId()).get();
        // 제대로 조회했는지 검증
        assertEquals(beforeMember.getId(), afterMember.getId());

        // when
        // 조회한 객체를 업데이트
        afterMember.updateMember("updatedPWD","010123",LocalDateTime.now());
        // 업데이트한 객체를 DB에 저장을 안함
//        memberRepository.save(afterMember);
//        em.flush();
//        em.clear();
        // 업데이트후 DB에 저장한 객체를 다시 조회
        Member afterUpdateMember = memberRepository.findById(afterMember.getId()).get();

        // when
            // 업데이트 전과 후 pwd 비교 -> 달라야 통과
        assertNotEquals(beforeMember.getPwd(), afterUpdateMember.getPwd());
    }

    @Test
    public void 멤버_삭제_테스트() {
        // given
            // 멤버 객체 생성
        Member beforeMember = Member.createMember("email@email.com","pwd123","name123",0, LocalDateTime.now(),"01010101100",true);
         // 멤버 객체 DB에 저장
        memberRepository.save(beforeMember);
        em.flush();
        em.clear();
            // DB에 저장한 멤버 객체 조회
        Member afterMember = memberRepository.findById(beforeMember.getId()).get();
            // 제대로 조회했는지 검증
        assertEquals(beforeMember.getId(), afterMember.getId());

        // when
            // 조회한 객체 삭제
        memberRepository.delete(afterMember);
        em.flush();
        em.clear();

        // then
            // DB에서 삭제된 멤버 정보에 대해 잘 삭제되었는지 확인
        Member member = memberRepository.findById(beforeMember.getId()).orElse(null);
        assertNull(member);
    }

}