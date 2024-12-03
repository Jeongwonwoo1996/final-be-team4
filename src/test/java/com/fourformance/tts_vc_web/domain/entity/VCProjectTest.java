package com.fourformance.tts_vc_web.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.repository.MemberAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import com.fourformance.tts_vc_web.repository.VCDetailRepository;
import com.fourformance.tts_vc_web.repository.VCProjectRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
//@Rollback(false)
class VCProjectTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private VCProjectRepository vcProjectRepository;

    @Autowired
    private VCDetailRepository vcDetailRepository;

    @Autowired
    private MemberAudioMetaRepository memberAudioMetaRepository;

    @PersistenceContext
    EntityManager em;


    public Member createTestMember() {
        Member member = Member.createMember("aaa@aaa.com", "1234", "imsi", 0, LocalDateTime.now(), "01012341234", true);

        return member;
    }


    // 1. 저장 테스트
    @Test
    @DisplayName("VCProjectCreateTest")
    public void createVCProject() {

        Member member = createTestMember();
        // 생성된 멤버를 저장한다.
        memberRepository.save(member);
        // 프로젝트 생성한다.
        VCProject vcProject = VCProject.createVCProject(member, "VC프로젝트1");
        // 프로젝트를 저장한다.
        vcProjectRepository.save(vcProject);
        // 쌓인 쿼리문을 쭉 날리고
        em.flush();
        // 영속성컨텍스트를 비운다.
        em.clear();
        // 프로젝트를 id로 찾고, 없으면 null반환한다.
        VCProject afterVCProject = vcProjectRepository.findById(vcProject.getId()).orElse(null);
        // assertNotNull을 통해서 afterVCProject가 있는지 확인하고,
        assertNotNull(afterVCProject);
        // 기존에 생성한 VCProject와 Id로 꺼낸 VCProject가 같은 프로젝트인지 확인한다.
        assertEquals(vcProject.getId(), afterVCProject.getId());

    }

    // 2.삭제 테스트
    @Test
    @DisplayName("VCProject 삭제테스트")
    public void deleteVCProject() {
        // 멤버를 생성하고
        Member member = createTestMember();
        // 멤버를 저장하고
        memberRepository.save(member);
        // VCProject를 생성하고 저장한다.
        VCProject vcProject = VCProject.createVCProject(member, "삭제할 프로젝트");
        vcProjectRepository.save(vcProject);
        em.flush();
        em.clear();
        // VC프로젝트를 Id로 찾고,
        VCProject afterVCProject = vcProjectRepository.findById(vcProject.getId()).orElse(null);
        // VCProject가 NotNull이면?
        assertNotNull(afterVCProject);
        //VCProject삭제

        vcProjectRepository.deleteById(afterVCProject.getId());
        VCProject deletedVCProject = vcProjectRepository.findById(afterVCProject.getId()).orElse(null);
        em.flush();
        em.clear();
        assertNull(deletedVCProject);
    }


    // 3. 업데이트 테스트
    @Test
    @DisplayName("업데이트 테스트 ")
    public void updateVCProject() {

        // given
        // 멤버를 생성하고
        Member member = createTestMember();
        // 멤버를 저장하고
        memberRepository.save(member);
        // VCProject를 생성하고 저장한다.
        VCProject vcProject = VCProject.createVCProject(member, "업데이트할 프로젝트");
        vcProjectRepository.save(vcProject);
        //타켓 오디오 메타 생성하고 저장하고
        MemberAudioMeta targetAudioMeta = MemberAudioMeta.createMemberAudioMeta(member, null, "/경로1", AudioType.VC_TRG);
        memberAudioMetaRepository.save(targetAudioMeta);

//        em.flush();
//        em.clear();

        //when
        VCProject findVCProject = vcProjectRepository.findById(vcProject.getId()).orElse(null);
        findVCProject.updateVCProject("업데이트된 프로젝트", targetAudioMeta);

        vcProjectRepository.save(findVCProject);
        em.flush();
        em.clear();

        VCProject afterVCProject = vcProjectRepository.findById(findVCProject.getId()).orElse(null);

        //then
        assertEquals(afterVCProject.getProjectName(), findVCProject.getProjectName());

    }
}
