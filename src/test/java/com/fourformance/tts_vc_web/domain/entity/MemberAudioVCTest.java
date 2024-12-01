package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.repository.MemberAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.MemberAudioVCRepository;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import com.fourformance.tts_vc_web.repository.VCProjectRepository;
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
//@Rollback(value = false)
class MemberAudioVCTest {

    @Autowired
    MemberAudioVCRepository memberAudioVCRepository;

    @Autowired
    VCProjectRepository vcProjectRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberAudioMetaRepository memberAudioMetaRepository;

    @PersistenceContext
    EntityManager em;

    // Member 객체를 한 번만 생성하여 공유
    public Member createTestMember() {
        Member member = Member.createMember("aaa@aaa.com", "1234", "imsi", 0, LocalDateTime.now(), "01012341234",true);
        return member;
    }

    public VCProject createTestVCProject(Member member) {
        VCProject vcProject = VCProject.createVCProject(member, "VCProject1");
        return vcProject;
    }

    public MemberAudioMeta createTestMemberAudioMeta_SRC(Member member) {
        MemberAudioMeta memberAudioMeta_SRC = MemberAudioMeta.createMemberAudioMeta(member, null, "/경로1", AudioType.VC_SRC);
        return memberAudioMeta_SRC;
    }
    public MemberAudioMeta createTestMemberAudioMeta_TRG(Member member) {
        MemberAudioMeta memberAudioMeta_TRG = MemberAudioMeta.createMemberAudioMeta(member, null, "/경로1", AudioType.VC_TRG);
        return memberAudioMeta_TRG;
    }

    @Test
    @DisplayName("생성 테스트")
    public void createTestMemberVCProject() {
        // given
        Member member = createTestMember();
        memberRepository.save(member);

        VCProject vcProject = createTestVCProject(member);
        vcProjectRepository.save(vcProject);

        // 소스오디오저장.
        MemberAudioMeta memberAudioMeta_src = createTestMemberAudioMeta_SRC(member);
        memberAudioMetaRepository.save(memberAudioMeta_src);

        // 타켓오디오저장.
        MemberAudioMeta memberAudioMeta_trg = createTestMemberAudioMeta_TRG(member);
        memberAudioMetaRepository.save(memberAudioMeta_trg);


        MemberAudioVC memberAudioVC_SRCFile = MemberAudioVC.createMemberAudioVC(memberAudioMeta_src, vcProject);
        memberAudioVCRepository.save(memberAudioVC_SRCFile);

        MemberAudioVC memberAudioVC_TRGFile = MemberAudioVC.createMemberAudioVC(memberAudioMeta_trg, vcProject);
        memberAudioVCRepository.save(memberAudioVC_TRGFile);

        em.flush();
        em.clear();

        MemberAudioVC findMemberAudioVC_SRC = memberAudioVCRepository.findById(memberAudioVC_SRCFile.getId()).orElse(null);
        assertNotNull(findMemberAudioVC_SRC);
        assertEquals(memberAudioVC_SRCFile.getId(), findMemberAudioVC_SRC.getId());
        MemberAudioVC findMemberAudioVC_TRG = memberAudioVCRepository.findById(memberAudioVC_TRGFile.getId()).orElse(null);
        assertNotNull(findMemberAudioVC_TRG);
    }

    @Test
    @DisplayName("MemberAudioVC 삭제 테스트")
    public void deleteMemberAudioVC() {
        // 1. 멤버를 생성하고 저장
        Member member = createTestMember();
        memberRepository.save(member);

        // 2. VCProject를 생성하고 저장
        VCProject vcProject = VCProject.createVCProject(member, "삭제할 프로젝트");
        vcProjectRepository.save(vcProject);

        // 3. MemberAudioMeta 생성
        MemberAudioMeta memberAudioMeta = MemberAudioMeta.createMemberAudioMeta(member, null, "/경로1", AudioType.VC_TRG);
        memberAudioMetaRepository.save(memberAudioMeta);

        // 4. MemberAudioVC 생성하고 저장
        MemberAudioVC memberAudioVC = MemberAudioVC.createMemberAudioVC(memberAudioMeta, vcProject);
        memberAudioVCRepository.save(memberAudioVC);

        em.flush();
        em.clear();

        // 5. MemberAudioVC가 저장되었는지 확인
        MemberAudioVC afterMemberAudioVC = memberAudioVCRepository.findById(memberAudioVC.getId()).orElse(null);
        assertNotNull(afterMemberAudioVC);

        // 6. MemberAudioVC 삭제
        memberAudioVCRepository.deleteById(afterMemberAudioVC.getId());
        em.flush();
        em.clear();

        // 7. MemberAudioVC가 삭제되었는지 확인
        MemberAudioVC deletedMemberAudioVC = memberAudioVCRepository.findById(afterMemberAudioVC.getId()).orElse(null);
        assertNull(deletedMemberAudioVC); // null이면 트루반환

    }


    // 업데이트??..교차테이블 업데이트해야하는지..





}
