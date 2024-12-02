package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.common.constant.APIStatusConst;
import com.fourformance.tts_vc_web.repository.TTSProjectRepository;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
class TTSProjectTest_team_api {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TTSProjectRepository ttsProjectRepository;

    @Autowired
    private MemberRepository memberRepository;

    // 1. TTS 프로젝트 생성 테스트
    @Test
    public void TTS프로젝트_생성_테스트() {
        // given - 멤버와 TTS 프로젝트 객체 생성
        Member member = Member.createMember("test@example.com", "password", "Alice", 1,
                LocalDateTime.now(), "010-1234-5678",true);
        memberRepository.save(member);

        TTSProject project = TTSProject.createTTSProject(member, "Sample Project",null,null,null,null,null);
        ttsProjectRepository.save(project);
        em.flush();
        em.clear();

        // when - 생성된 TTS 프로젝트 조회
        TTSProject foundProject = ttsProjectRepository.findById(project.getId()).get();

        // then - 생성된 프로젝트 검증
        assertEquals(project.getId(), foundProject.getId());
        assertEquals("Sample Project", foundProject.getProjectName());
        assertEquals(member.getId(), foundProject.getMember().getId());
    }

    // 2. TTS 프로젝트 업데이트 테스트
    @Test
    public void TTS프로젝트_업데이트_테스트() {
        // given - 멤버와 TTS 프로젝트 객체 생성 후 저장
        Member member = Member.createMember("test@example.com", "password", "Alice", 1,
                LocalDateTime.now(), "010-1234-5678",true);
        memberRepository.save(member);

        TTSProject project = TTSProject.createTTSProject(member, "Old Project",null,null,null,null,null);
        ttsProjectRepository.save(project);
        em.flush();
        em.clear();

        TTSProject foundProject = ttsProjectRepository.findById(project.getId()).get();

        // when - TTS 프로젝트 업데이트 및 저장
        foundProject.updateTTSProject("Updated Project", null,"New Script", 1.2f, 0.8f, 0.9f);
        ttsProjectRepository.save(foundProject);
        em.flush();
        em.clear();

        TTSProject updatedProject = ttsProjectRepository.findById(foundProject.getId()).get();

        // then - 업데이트된 프로젝트 검증
        assertEquals("Updated Project", updatedProject.getProjectName());
        assertEquals("New Script", updatedProject.getFullScript());
        assertEquals(1.2f, updatedProject.getGlobalSpeed());
    }

    // 3. TTS 프로젝트 API 상태 업데이트 테스트
    @Test
    public void TTS프로젝트_API상태_업데이트_테스트() {
        // given - 멤버와 TTS 프로젝트 객체 생성 후 저장
        Member member = Member.createMember("test@example.com", "password", "Alice", 1,
                LocalDateTime.now(), "010-1234-5678",true);
        memberRepository.save(member);

        TTSProject project = TTSProject.createTTSProject(member, "API Project",null,null,null,null,null);
        ttsProjectRepository.save(project);
        em.flush();
        em.clear();

        TTSProject foundProject = ttsProjectRepository.findById(project.getId()).get();

        // when - API 상태 업데이트 및 저장
        foundProject.updateAPIStatus(APIStatusConst.IN_PROGRESS);
        ttsProjectRepository.save(foundProject);
        em.flush();
        em.clear();

        TTSProject updatedProject = ttsProjectRepository.findById(foundProject.getId()).get();

        // then - 업데이트된 API 상태 검증
        assertEquals(APIStatusConst.IN_PROGRESS, updatedProject.getApiStatus());
        assertNotNull(updatedProject.getAPIStatusModifiedAt());
    }

    // 4. TTS 프로젝트 삭제 테스트
    @Test
    public void TTS프로젝트_삭제_테스트() {
        // given - 멤버와 TTS 프로젝트 객체 생성 후 저장
        Member member = Member.createMember("test@example.com", "password", "Alice", 1,
                LocalDateTime.now(), "010-1234-5678",true);
        memberRepository.save(member);

        TTSProject project = TTSProject.createTTSProject(member, "Delete Test Project",null,null,null,null,null);
        ttsProjectRepository.save(project);
        em.flush();
        em.clear();

        TTSProject foundProject = ttsProjectRepository.findById(project.getId()).get();

        // when - TTS 프로젝트 삭제
        ttsProjectRepository.delete(foundProject);
        em.flush();
        em.clear();

        // then - 삭제된 TTS 프로젝트 검증
        TTSProject deletedProject = ttsProjectRepository.findById(project.getId()).orElse(null);
        assertNull(deletedProject);
    }
}
