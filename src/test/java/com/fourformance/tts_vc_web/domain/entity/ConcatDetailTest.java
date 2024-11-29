package com.fourformance.tts_vc_web.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fourformance.tts_vc_web.repository.ConcatDetailRepository;
import com.fourformance.tts_vc_web.repository.ConcatProjectRepository;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
//@Rollback(value = false)
class ConcatDetailTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ConcatProjectRepository concatProjectRepository;

    @Autowired
    ConcatDetailRepository concatDetailRepository;

    public Member createTestMember() {
        Member member = Member.createMember("test@test.com", "1234", "testName", 0, LocalDateTime.now(),
                "010-1234-1234",true);
        return member;
    }

    @Test
    public void ConcatDetail_생성_테스트() {
        Member m = createTestMember();
        memberRepository.save(m);

        em.flush();
        em.clear();

        // Concat 프로젝트 생성
        ConcatProject beforeConcatProject = ConcatProject.createConcatProject(m, "new project");
        concatProjectRepository.save(beforeConcatProject);

        em.flush();
        em.clear();

        // Concat Detail 생성
        ConcatDetail beforeConcatDetail = ConcatDetail.createConcatDetail(beforeConcatProject, 1, false, "안녕하세요", 1.0F,
                null);
        concatDetailRepository.save(beforeConcatDetail);
        em.flush();
        em.clear();

        //when
        //DB에 저장한 ConcatProject, ConcatDetail 객체 조회
        ConcatProject afterConcatProject = concatProjectRepository.findById(beforeConcatProject.getId()).get();
        ConcatDetail afterConcatDetail = concatDetailRepository.findById(beforeConcatDetail.getId()).get();

        // 조회한 ConcatProject, ConcatProDetail 일치하는지 검증
        assertEquals(beforeConcatProject.getId(), afterConcatProject.getId());
        assertEquals(beforeConcatDetail.getId(), afterConcatDetail.getId());

    }

    @Test
    public void ConcatDetail_변경_테스트() {
        Member m = createTestMember();
        memberRepository.save(m);

        em.flush();
        em.clear();

        // Concat 프로젝트 생성
        ConcatProject beforeConcatProject = ConcatProject.createConcatProject(m, "new project");
        concatProjectRepository.save(beforeConcatProject);

        em.flush();
        em.clear();

        // Concat Detail 생성
        ConcatDetail beforeConcatDetail = ConcatDetail.createConcatDetail(beforeConcatProject, 1, false, "안녕하세요", 1.0F,
                null);
        concatDetailRepository.save(beforeConcatDetail);
        em.flush();
        em.clear();

        //when
        //DB에 저장한 ConcatProject, ConcatDetail 객체 조회
        ConcatProject afterConcatProject = concatProjectRepository.findById(beforeConcatProject.getId()).get();
        ConcatDetail afterConcatDetail = concatDetailRepository.findById(beforeConcatDetail.getId()).get();

        // 조회한 ConcatProject, ConcatProDetail 일치하는지 검증
        assertEquals(beforeConcatProject.getId(), afterConcatProject.getId());
        assertEquals(beforeConcatDetail.getId(), afterConcatDetail.getId());

        // ConcatDetail 수정 후 DB 저장
//        afterConcatDetail.updateDetails(3, true, "야호야호", 2.0F, false);
//        concatDetailRepository.save(afterConcatDetail);
//        em.flush();
//        em.clear();

        // 업데이트 후 DB에 저장한 객체를 다시 조회
        ConcatDetail afterUpdateConcatDetail = concatDetailRepository.findById(afterConcatDetail.getId()).get();

        //then
        //업데이트 전과 후 데이터 비교 -> 달라야 성공
//        assertNotEquals(beforeConcatDetail.getEndSilence(), afterUpdateConcatDetail.getEndSilence());

        // 업데이트 후 EndSilence 인증
//        assertEquals(2.0F, afterConcatDetail.getEndSilence());

    }

    @Test
    public void ConcatDetail_삭제_테스트() {
        Member m = createTestMember();
        memberRepository.save(m);

        em.flush();
        em.clear();

        // Concat 프로젝트 생성
        ConcatProject beforeConcatProject = ConcatProject.createConcatProject(m, "new project");
        concatProjectRepository.save(beforeConcatProject);

        em.flush();
        em.clear();

        // Concat Detail 생성
        ConcatDetail beforeConcatDetail = ConcatDetail.createConcatDetail(beforeConcatProject, 1, false, "안녕하세요", 1.0F,
                null);
        concatDetailRepository.save(beforeConcatDetail);
        em.flush();
        em.clear();

        //when
        //DB에 저장한 ConcatProject, ConcatDetail 객체 조회
        ConcatProject afterConcatProject = concatProjectRepository.findById(beforeConcatProject.getId()).get();
        ConcatDetail afterConcatDetail = concatDetailRepository.findById(beforeConcatDetail.getId()).get();

        // 조회한 ConcatProject, ConcatProDetail 일치하는지 검증
        assertEquals(beforeConcatProject.getId(), afterConcatProject.getId());
        assertEquals(beforeConcatDetail.getId(), afterConcatDetail.getId());

        //when
        // 조회된 객체 삭제
        concatDetailRepository.delete(afterConcatDetail);
        em.flush();
        em.clear();

        //then
        //DB에서 삭제된 concatDetail 정보에 대해 잘 삭제되었는지 확인
        ConcatDetail concatDetail = concatDetailRepository.findById(beforeConcatDetail.getId()).orElse(null);
        assertNull(concatDetail);
    }
}