package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.common.constant.ConcatStatusConst;
import com.fourformance.tts_vc_web.repository.ConcatProjectRepository;
import com.fourformance.tts_vc_web.repository.ConcatStatusHistoryRepository;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ConcatStatusHistoryTest {

    @Autowired
    private ConcatStatusHistoryRepository ConcatStatusHistoryRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ConcatProjectRepository concatProjectRepository;
    @PersistenceContext
    private EntityManager em;

    public Member 멤버_생성기() {
        // Member 객체 생성
        Member member = Member.createMember("email.com", "pwd123", "이의준", 0, LocalDateTime.now(), "01012341234",true);
        // Member 객체 DB에 저장
        memberRepository.save(member);
        em.flush();
        em.clear();

        return member;
    }

//    public ConcatProject 컨캣_프로젝트_생성기() {
//        Member member = 멤버_생성기();
//        // Concat 프로젝트 객체 생성
//        ConcatProject concatProject = ConcatProject.createConcatProject(member,"컨캣1");
//        // Concat 프로젝트 객체 DB에 저장
//        concatProjectRepository.save(concatProject);
//        return concatProject;
//    }

    @Test
    public void 컨캣_상태_생성_테스트() {
        // given
        // 멤버, 컨캣 프로젝트 객체 생성 후 DB에 저장
        Member member = 멤버_생성기();
        ConcatProject concatProject = ConcatProject.createConcatProject(member,"컨캣_프로젝트_1");
        concatProjectRepository.save(concatProject);
        em.flush();
        em.clear();

        // when
        // Concat_상태_이력 객체 생성
        ConcatStatusHistory beforeConcatStatusHistory = ConcatStatusHistory.createConcatStatusHistory(concatProject, ConcatStatusConst.SUCCESS);
        // Concat_상태 이력 객체 DB에 저장
        ConcatStatusHistoryRepository.save(beforeConcatStatusHistory);
        em.flush();
        em.clear();
        // 저장한 Concat_상태_이력 조회
        ConcatStatusHistory afterConcatStatusHistory = ConcatStatusHistoryRepository.findById(beforeConcatStatusHistory.getId()).get();


        // then
        // 생성한 객체와 DB에서 조회한 객체 비교
        // 컨캣_상태 비교
        assertEquals(beforeConcatStatusHistory.getConcatStatusConst(), afterConcatStatusHistory.getConcatStatusConst());
        assertEquals(ConcatStatusConst.SUCCESS, afterConcatStatusHistory.getConcatStatusConst());
        // 컨캣_프로젝트 이름 비교
        System.out.println("afterConcatStatusHistory.getConcatProject().getProjectName() = " + afterConcatStatusHistory.getConcatProject().getProjectName());
        System.out.println("beforeConcatStatusHistory.getConcatProject().getProjectName() = " + beforeConcatStatusHistory.getConcatProject().getProjectName());
        assertEquals(beforeConcatStatusHistory.getConcatProject().getProjectName(), afterConcatStatusHistory.getConcatProject().getProjectName());
        assertEquals("컨캣_프로젝트_1", afterConcatStatusHistory.getConcatProject().getProjectName());
    }

    @Test
    public void 컨캣_상태_생성_실패_테스트() {
        // given
        // 멤버, 컨캣 프로젝트 객체 생성 후 DB에 저장
        Member member = 멤버_생성기();
        ConcatProject concatProject = ConcatProject.createConcatProject(member,"컨캣_프로젝트_1");

        // when
        // Concat_상태_이력 객체 생성
        ConcatStatusHistory beforeConcatStatusHistory = ConcatStatusHistory.createConcatStatusHistory(concatProject, ConcatStatusConst.SUCCESS);
        // Concat_상태 이력 객체 DB에 저장하지 않음 -> 실패 테스트를 위해
//        ConcatStatusHistoryRepository.save(beforeConcatStatusHistory);

        // then
        // 예외가 발생해야 하는 부분 검증
        assertThrows(Exception.class, () -> {
            // DB에 저장하지 않은 상태 이력을 조회
            ConcatStatusHistory afterConcatStatusHistory = ConcatStatusHistoryRepository.findById(beforeConcatStatusHistory.getId()).get();
        });
    }

    @Test
    public void 컨캣_상태_업데이트_테스트() {
//        // given
//            // 멤버, 컨캣 프로젝트 객체 생성 후 DB에 저장
//        Member member = 멤버_생성기();
//        ConcatProject concatProject = ConcatProject.createConcatProject(member,"컨캣_프로젝트_1");
//
//        // when
//            // Concat_상태_이력 객체 생성
//        ConcatStatusHistory beforeConcatStatusHistory = ConcatStatusHistory.createConcatStatusHistory(concatProject, ConcatStatusConst.SUCCESS);
//            // Concat_상태 이력 객체 DB에 저장
//        ConcatStatusHistoryRepository.save(beforeConcatStatusHistory);
//            // 저장한 Concat_상태_이력 조회
//        ConcatStatusHistory afterConcatStatusHistory = ConcatStatusHistoryRepository.findById(beforeConcatStatusHistory.getId()).get();
//            // 제대로 저장되었는지 검증
//        assertEquals(beforeConcatStatusHistory, afterConcatStatusHistory);
//            // 조회한 객체 업데이트
//        afterConcatStatusHistory.updateStatus(ConcatStatusConst.SUCCESS);
//            // 업데이트한 객체 DB에 저장
//        concatProjectRepository.save(concatProject);
//
//        // then
//            // 저장한 DB 객체를 다시 조회하여 제대로 업데이트되었는지 확인
//        ConcatStatusHistory afterUpdateConcatHistory = ConcatStatusHistoryRepository.findById(afterConcatStatusHistory.getId()).get();
//        assertEquals(afterConcatStatusHistory, afterUpdateConcatHistory);
//        assertEquals(ConcatStatusConst.SUCCESS, afterUpdateConcatHistory.getConcatStatusConst());


        // 컨캣 상태 이력은 업데이트가 없다....

    }

    @Test
    public void 컨캣_상태_삭제_테스트() {

        // 이력 엔티티는 삭제 될 일이 없다
    }

}