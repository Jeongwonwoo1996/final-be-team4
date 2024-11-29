package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.repository.*;
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
class TTSDetailTest {

    @Autowired
    private TTSProjectRepository ttsProjectRepository;

    @Autowired
    private TTSDetailRepository ttsDetailRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private VoiceStyleRepository VoiceStyleRepository;

    @PersistenceContext
    private EntityManager em;

    // 테스트용 Member 객체 생성 메서드
    public Member createTestMember() {
        // 멤버 생성
        return Member.createMember("test@example.com", "password", "Test User", 1, LocalDateTime.now(), "010-1234-5678",true);
    }

    // 테스트용 TTSProject 객체 생성 메서드
    public TTSProject createTestTTSProject(Member member) {
        // TTS 프로젝트 생성
        return TTSProject.createTTSProject(member, "Test TTS Project", null,null, 1.0f,1.0f,1.0f);
    }

    // 테스트용 VoiceStyle 객체 생성 메서드
    public VoiceStyle createTestStyle() {
        // 스타일 생성
        VoiceStyle voiceStyle = VoiceStyle.createVoiceStyle("대한민국", "ko-KR", "standard", "수연", "female", "차분한");
        VoiceStyleRepository.save(voiceStyle);
        return voiceStyle;
    }

    // 1. TTSDetail 생성 테스트
    @Test
    @DisplayName("TTSDetail 생성 테스트")
    public void createTTSDetail() {

        // given
        // 멤버 생성 및 저장
        Member member = createTestMember();
        memberRepository.saveAndFlush(member);

        // TTS 프로젝트 생성 및 저장
        TTSProject ttsProject = createTestTTSProject(member);
        ttsProjectRepository.saveAndFlush(ttsProject);

        // 스타일 생성 및 저장
        VoiceStyle VoiceStyle = createTestStyle();

        for (int i = 1; i < 11; i++) {
            // when
            // TTSDetail 생성 및 저장
            TTSDetail ttsDetail = TTSDetail.createTTSDetail(ttsProject, "Sample Script", i);
            ttsDetailRepository.save(ttsDetail);

            // DB에 반영하고 영속성 컨텍스트 초기화
            em.flush();
            em.clear();

            // TTSDetail 객체 조회
            TTSDetail afterTTSDetail = ttsDetailRepository.findById(ttsDetail.getId()).orElse(null);

            // then
            // 조회된 객체가 null이 아닌지 확인
            assertNotNull(afterTTSDetail);
            // 생성된 TTSDetail의 필드 값 검증
            assertEquals(ttsDetail.getUnitScript(), afterTTSDetail.getUnitScript());
            assertEquals(ttsDetail.getUnitSequence(), afterTTSDetail.getUnitSequence());
        }
    }

    // 2. TTSDetail 업데이트 테스트
    @Test
    @DisplayName("TTSDetail 업데이트 테스트")
    public void updateTTSDetail() {

        // given
        // 멤버 생성 및 저장
        Member member = createTestMember();
        memberRepository.saveAndFlush(member);

        // TTS 프로젝트 생성 및 저장
        TTSProject ttsProject = createTestTTSProject(member);
        ttsProjectRepository.saveAndFlush(ttsProject);

        // 스타일 생성 및 저장
        VoiceStyle VoiceStyle = createTestStyle();

        // TTSDetail 생성 및 저장
        TTSDetail ttsDetail = TTSDetail.createTTSDetail(ttsProject, "Old Script", 1);
        ttsDetailRepository.saveAndFlush(ttsDetail);

        // DB에 반영하고 영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        // when
        // TTSDetail 객체 조회 및 업데이트
        TTSDetail foundTTSDetail = ttsDetailRepository.findById(ttsDetail.getId()).orElse(null);
        foundTTSDetail.updateTTSDetail(VoiceStyle, "Updated Script", 1.5f, 0.5f, 0.8f, 2, false);

        // 업데이트된 TTSDetail 저장
        ttsDetailRepository.save(foundTTSDetail);

        // DB에 반영하고 영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        // 업데이트된 TTSDetail 객체 조회
        TTSDetail afterUpdateTTSDetail = ttsDetailRepository.findById(foundTTSDetail.getId()).orElse(null);

        // then
        // 업데이트된 필드 값 검증
        assertNotNull(afterUpdateTTSDetail);
        assertEquals("Updated Script", afterUpdateTTSDetail.getUnitScript());
        assertEquals(1.5f, afterUpdateTTSDetail.getUnitSpeed());
        assertEquals(0.5f, afterUpdateTTSDetail.getUnitPitch());
        assertEquals(0.8f, afterUpdateTTSDetail.getUnitVolume());
        assertEquals(2, afterUpdateTTSDetail.getUnitSequence());
    }

    // 3. TTSDetail 삭제 테스트
    @Test
    @DisplayName("TTSDetail 삭제 테스트")
    public void deleteTTSDetail() {

        // given
        // 멤버 생성 및 저장
        Member member = createTestMember();
        memberRepository.saveAndFlush(member);

        // TTS 프로젝트 생성 및 저장
        TTSProject ttsProject = createTestTTSProject(member);
        ttsProjectRepository.saveAndFlush(ttsProject);

        // TTSDetail 생성 및 저장
        TTSDetail ttsDetail = TTSDetail.createTTSDetail(ttsProject, "Delete Test Script", 1);
        ttsDetailRepository.saveAndFlush(ttsDetail);

        // DB에 반영하고 영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        // when
        // TTSDetail 객체 조회 및 삭제
        TTSDetail foundTTSDetail = ttsDetailRepository.findById(ttsDetail.getId()).orElse(null);
        foundTTSDetail.deleteTTSDetail();

        // 삭제된 TTSDetail 저장
        ttsDetailRepository.save(foundTTSDetail);
        em.flush();
        em.clear();

        // 삭제된 TTSDetail 객체 조회
        TTSDetail afterDeleteTTSDetail = ttsDetailRepository.findById(foundTTSDetail.getId()).orElse(null);

        // then
        // 삭제 여부 검증
        assertNotNull(afterDeleteTTSDetail);
        assertTrue(afterDeleteTTSDetail.getIsDeleted());
        assertNotNull(afterDeleteTTSDetail.getDeletedAt());
    }
}
