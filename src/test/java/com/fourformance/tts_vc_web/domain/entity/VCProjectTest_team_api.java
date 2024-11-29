package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.repository.MemberAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import com.fourformance.tts_vc_web.repository.VCDetailRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
class VCProjectTest_team_api {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberAudioMetaRepository memberAudioMetaRepository;
    @Autowired
    private VCProjectRepository vcProjectRepository;
    @Autowired
    private VCDetailRepository vcDetailRepository;

    @PersistenceContext
    private EntityManager em;

    // 테스트용 Member 생성
    private Member createTestMember() {
        return Member.createMember("test@user.com", "1234", "Test User", 0, LocalDateTime.now(), "01012345678",true);
    }

    // 테스트용 MemberAudioMeta 생성
    private MemberAudioMeta createTestAudioMeta(Member member, String audioUrl, AudioType audioType) {
        return MemberAudioMeta.createMemberAudioMeta(member, null, audioUrl, audioType);
    }

    @Test
    @DisplayName("VCProject 생성 및 연동된 VCDetail 생성 테스트")
    void createVCProjectWithDetails() {
        // Given
        Member member = createTestMember();
        memberRepository.save(member);

        MemberAudioMeta audioMeta = createTestAudioMeta(member, "/audio/path", AudioType.VC_TRG);
        memberAudioMetaRepository.save(audioMeta);

        VCProject vcProject = VCProject.createVCProject(member, "Test Project");
        vcProjectRepository.save(vcProject);

        VCDetail vcDetail = VCDetail.createVCDetail(vcProject, audioMeta);
        vcDetailRepository.save(vcDetail);

        em.flush();
        em.clear();

        // When
        VCProject savedProject = vcProjectRepository.findById(vcProject.getId()).orElse(null);
        List<VCDetail> details = vcDetailRepository.findByVcProject(savedProject);

        // Then
        assertNotNull(savedProject);
        assertEquals("Test Project", savedProject.getProjectName());
        assertEquals(1, details.size());
        assertEquals(audioMeta.getAudioUrl(), details.get(0).getMemberAudioMeta().getAudioUrl());
    }

    @Test
    @DisplayName("VCDetail 업데이트 테스트")
    void updateVCDetail() {
        // Given
        Member member = createTestMember();
        memberRepository.save(member);

        MemberAudioMeta audioMeta = createTestAudioMeta(member, "/audio/path", AudioType.VC_TRG);
        memberAudioMetaRepository.save(audioMeta);

        VCProject vcProject = VCProject.createVCProject(member, "Test Project");
        vcProjectRepository.save(vcProject);

        VCDetail vcDetail = VCDetail.createVCDetail(vcProject, audioMeta);
        vcDetailRepository.save(vcDetail);

        em.flush();
        em.clear();

        // When
        VCDetail savedDetail = vcDetailRepository.findById(vcDetail.getId()).orElse(null);
        assertNotNull(savedDetail);
        savedDetail.updateDetails(true, "Updated Script");
        vcDetailRepository.save(savedDetail);

        em.flush();
        em.clear();

        VCDetail updatedDetail = vcDetailRepository.findById(vcDetail.getId()).orElse(null);

        // Then
        assertNotNull(updatedDetail);
        assertTrue(updatedDetail.getIsChecked());
        assertEquals("Updated Script", updatedDetail.getUnitScript());
    }

    @Test
    @DisplayName("VCProject 삭제 테스트 - 연관된 VCDetail 삭제")
    public void deleteVCProjectWithDetails() {
        // given
        Member member = createTestMember();
        memberRepository.save(member);

        VCProject vcProject = VCProject.createVCProject(member, "Test Project");
        vcProjectRepository.save(vcProject);

        MemberAudioMeta audioMeta = MemberAudioMeta.createMemberAudioMeta(
                member, null, "/audio/path", AudioType.VC_TRG);
        memberAudioMetaRepository.save(audioMeta);

        VCDetail vcDetail = VCDetail.createVCDetail(vcProject, audioMeta);
        vcDetailRepository.save(vcDetail);

        em.flush();
        em.clear();

        // when
        VCProject loadedProject = vcProjectRepository.findById(vcProject.getId()).orElseThrow();
        List<VCDetail> details = vcDetailRepository.findByVcProject(loadedProject);

        details.forEach(vcDetailRepository::delete); // 연관된 VCDetail 삭제
        vcProjectRepository.delete(loadedProject);  // VCProject 삭제

        em.flush();
        em.clear();

        // then
        assertNull(vcProjectRepository.findById(vcProject.getId()).orElse(null));
    }
}
