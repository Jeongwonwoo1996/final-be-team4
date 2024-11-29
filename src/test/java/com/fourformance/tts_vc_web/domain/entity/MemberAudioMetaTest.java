package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.repository.MemberAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value= false)
class MemberAudioMetaTest {


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAudioMetaRepository memberAudioMetaRepository;

    @PersistenceContext
    EntityManager em;

    public Member createTestMember() {
        Member member = Member.createMember("aaa@aaa.com","1234","JaneDoe",1, LocalDateTime.now(),"01011112222",true);
        memberRepository.save(member);
        return member;
    }

    @Test
    @DisplayName("생성테스트")
    public void 멤버오디오메타_생성테스트() {
        //given
        Member member = createTestMember();

        MemberAudioMeta memberAudioMeta = MemberAudioMeta.createMemberAudioMeta(member,null, "/경로2", AudioType.VC_SRC);


        //when
        memberAudioMetaRepository.save(memberAudioMeta);
        em.flush();
        em.clear();

        //test시작
        MemberAudioMeta findMemberAudioMeta = memberAudioMetaRepository.findById(memberAudioMeta.getId()).orElse(null);
        assertNotNull(findMemberAudioMeta);
        assertEquals(memberAudioMeta.getId(), findMemberAudioMeta.getId());
    }
    @Test
    @DisplayName("조회테스트")
    public void 멤버오디오메타_찾기테스트() {
        //given
        Member member = createTestMember();
        MemberAudioMeta memberAudioMeta = MemberAudioMeta.createMemberAudioMeta(member,null, "/경로2", AudioType.VC_SRC);
        memberAudioMetaRepository.save(memberAudioMeta);
        em.flush();
        em.clear();

        //when
        MemberAudioMeta foundMemberAudioMeta = memberAudioMetaRepository.findById(memberAudioMeta.getId()).orElse(null);

        //then
        assertNotNull(foundMemberAudioMeta);
        assertEquals(memberAudioMeta.getId(), foundMemberAudioMeta.getId());
    }


    @Test
    @DisplayName("업데이트테스트")
    public void 멤버오디오메타_업뎃테스트() {
        // given
        Member member = createTestMember();
        MemberAudioMeta memberAudioMeta = MemberAudioMeta.createMemberAudioMeta(member,null,"/경로2", AudioType.VC_SRC);
        memberAudioMetaRepository.save(memberAudioMeta);
        em.flush();
        em.clear();

        // when
        MemberAudioMeta savedAudioMeta = memberAudioMetaRepository.findById(memberAudioMeta.getId()).orElse(null);
        assertNotNull(savedAudioMeta);
        savedAudioMeta.delete(); // 삭제 메서드 호출
        em.flush();
        em.clear();

        // then : isDeleted가 업데이트되었는지와 삭제시간도 있는지 체크
        MemberAudioMeta updatedAudioMeta = memberAudioMetaRepository.findById(memberAudioMeta.getId()).orElse(null);
        assertNotNull(updatedAudioMeta);
        assertTrue(updatedAudioMeta.getIsDeleted());
        assertNotNull(updatedAudioMeta.getDeletedAt());
    }

    @Test
    @DisplayName("삭제테스트")
    public void 멤버오디오메타_삭제테스트(){
        // given
        Member member = createTestMember();
        MemberAudioMeta memberAudioMeta = MemberAudioMeta.createMemberAudioMeta(member,null, "/경로2", AudioType.VC_SRC);
        memberAudioMetaRepository.save(memberAudioMeta);
        em.flush();
        em.clear();

        //when
        memberAudioMetaRepository.deleteById(memberAudioMeta.getId());
        em.flush();
        em.clear();

        // then
        MemberAudioMeta deletedAudioMeta = memberAudioMetaRepository.findById(memberAudioMeta.getId()).orElse(null);
        assertNull(deletedAudioMeta);

    }



}