package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.repository.ConcatProjectRepository;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.midi.MetaMessage;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
//@Rollback(value = false)
class ConcatProjectTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ConcatProjectRepository concatProjectRepository;

    public Member createTestMember() {
        Member member = Member.createMember("test@test.com", "1234", "testName", 0, LocalDateTime.now(), "010-1234-1234",true);
        return member;
    }

    @Test
    @DisplayName("ConcatProject 엔티티 생성 테스트")
    public void ConcatProject_생성_테스트(){

        Member m = createTestMember();
        memberRepository.save(m);

        em.flush();
        em.clear();

        // Concat 프로젝트 생성
        ConcatProject beforeConcatProject = ConcatProject.createConcatProject(m, "new project");
        concatProjectRepository.save(beforeConcatProject);

        em.flush();
        em.clear();

        //when
            // DB에 저장한 ConcatProject 객체 조회
        ConcatProject afterConcatProject = concatProjectRepository.findById(beforeConcatProject.getId()).get();

        //then
            //두 객체의 id 비교
        assertEquals(beforeConcatProject.getId(), afterConcatProject.getId());
            //두 객체의 projectName 비교
        assertEquals(beforeConcatProject.getProjectName(), afterConcatProject.getProjectName());

    }

    @Test
    public void ConcatProject_업데이트_테스트(){

        Member m = createTestMember();
        memberRepository.save(m);

        em.flush();
        em.clear();

        // Concat 프로젝트 생성
        ConcatProject beforeConcatProject = ConcatProject.createConcatProject(m, "new project");
        concatProjectRepository.save(beforeConcatProject);

        em.flush();
        em.clear();

        //when
        // DB에 저장한 ConcatProject 객체 조회
        ConcatProject afterConcatProject = concatProjectRepository.findById(beforeConcatProject.getId()).get();

        //then
        //두 객체의 id 비교
        assertEquals(beforeConcatProject.getId(), afterConcatProject.getId());


        //when
            //조회된 객체를 업데이트
        afterConcatProject.updateConcatProject("updated Project Name", 1.0F, 1.0F);
            //업데이트한 객체를 DB에 저장
        concatProjectRepository.save(afterConcatProject);
        em.flush();
        em.clear();
            //업데이트 후 DB에 저장한 객체를 다시 조회
        ConcatProject afterUpdateConcatProject = concatProjectRepository.findById(afterConcatProject.getId()).get();

        //then
            //업데이트 전과 projectName 비교 -> 달라야 성공)
        assertNotEquals(beforeConcatProject.getProjectName(), afterConcatProject.getProjectName());
            //업데이트 후 projectName 검증
        assertEquals("updated Project Name", afterConcatProject.getProjectName());
    }

    @Test
    public void ConcatProject_삭제_테스트(){
        Member m = createTestMember();
        memberRepository.save(m);

        em.flush();
        em.clear();

        // Concat 프로젝트 생성
        ConcatProject beforeConcatProject = ConcatProject.createConcatProject(m, "new project");
        concatProjectRepository.save(beforeConcatProject);

        em.flush();
        em.clear();

        //when
        // DB에 저장한 ConcatProject 객체 조회
        ConcatProject afterConcatProject = concatProjectRepository.findById(beforeConcatProject.getId()).get();

        //then
        //두 객체의 id 비교
        assertEquals(beforeConcatProject.getId(), afterConcatProject.getId());

        //when
            // 조회된 객체 삭제
        concatProjectRepository.delete(afterConcatProject);
        em.flush();
        em.clear();

        //then
            //DB에서 삭제된 concatProject 정보에 대해서 잘 삭제되었는지 확인
        ConcatProject concatProject = concatProjectRepository.findById(beforeConcatProject.getId()).orElse(null);
        assertNull(concatProject);


    }





}