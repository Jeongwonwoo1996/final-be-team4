package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.common.constant.APIUnitStatusConst;
import com.fourformance.tts_vc_web.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apiguardian.api.API;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.fourformance.tts_vc_web.domain.entity.TTSProject.createTTSProject;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
class APIStatusTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    APIStatusRepository apiStatusRepository;

    @Autowired
    TTSProjectRepository ttsProjectRepository;

    @Autowired
    TTSDetailRepository ttsDetailRepository;

    @Autowired
    VCProjectRepository vcProjectRepository;

    @Autowired
    VCDetailRepository vcDetailRepository;

    public Member createTestMember() {
        Member member = Member.createMember("test@test.com", "1234", "testName", 0, LocalDateTime.now(), "010-1234-1234",true);
        return member;
    }


    @Test
    @DisplayName("APIStatus 엔티티 생성 테스트")
    public void createAPIStatus(){


        Member m = createTestMember();
        memberRepository.save(m);

        em.flush();
        em.clear();

        // TTS 프로젝트 생성
        TTSProject ttsProject = createTTSProject(m, "new Create test",null,null,null,null,null);
        ttsProjectRepository.save(ttsProject);

        em.flush();
        em.clear();

        //TTS detail 생성
        TTSDetail ttsDetail = TTSDetail.createTTSDetail(ttsProject, "unit Script test", 0);
        ttsDetailRepository.save(ttsDetail);

        em.flush();
        em.clear();

        //API Status 생성
        APIStatus apiStatus = APIStatus.createAPIStatus(null, ttsDetail, "test");

        apiStatusRepository.save(apiStatus);

        em.flush();
        em.clear();

        // APIStatus 조회

        APIStatus foundApiStatus1 = em.find(APIStatus.class, apiStatus.getId());
        APIStatus foundApiStatus2 = apiStatusRepository.findById(apiStatus.getId()).orElse(null);

        //조회한 APIStatus 일치하는지 검증

        assertEquals(foundApiStatus1.getId(), apiStatus.getId());
        assertEquals(foundApiStatus2.getId(), apiStatus.getId());

    }


    @Test
    public void APIStatus_업데이트_테스트(){

        //APIStatus 생성
        Member m = createTestMember();
        memberRepository.save(m);
        em.flush();
        em.clear();

        // TTS 프로젝트 생성
        TTSProject ttsProject = createTTSProject(m, "new Create test",null,null,null,null,null);
        ttsProjectRepository.save(ttsProject);
        em.flush();
        em.clear();

        //TTS detail 생성
        TTSDetail ttsDetail = TTSDetail.createTTSDetail(ttsProject, "unit Script test", 0);
        ttsDetailRepository.save(ttsDetail);
        em.flush();
        em.clear();

        //API Status 생성
        APIStatus beforeApiStatus = APIStatus.createAPIStatus(null, ttsDetail, "test");
        apiStatusRepository.save(beforeApiStatus);
        em.flush();
        em.clear();

        //DB에 저장한 APIStatus 객체 조회
        APIStatus afterApiStatus = apiStatusRepository.findById(beforeApiStatus.getId()).get();

        // 제대로 조회 됐는지 검증
        assertEquals(beforeApiStatus.getId(), afterApiStatus.getId());

        //APIStatus 수정 후 DB 저장
        afterApiStatus.updateResponseInfo("response200", 200, APIUnitStatusConst.SUCCESS);
        apiStatusRepository.save(afterApiStatus);
        em.flush();
        em.clear();

        // 업데이트 후 DB에 저장한 객체를 다시 조회
        APIStatus afterUpdateApiStatus = apiStatusRepository.findById(afterApiStatus.getId()).get();

        // then
            // 업데이트 전과 후 pwd비교 -> 달라야 성공
        assertNotEquals(beforeApiStatus.getResponseCode(), afterUpdateApiStatus.getResponseCode());

        //업데이트 후 pwd 검증
        assertEquals(200, afterApiStatus.getResponseCode());
    }

    @Test
    public void APIStatus_삭제_테스트(){
        //APIStatus 생성
        Member m = createTestMember();
        memberRepository.save(m);
        em.flush();
        em.clear();

        // TTS 프로젝트 생성
        TTSProject ttsProject = createTTSProject(m, "new Create test",null,null,null,null,null);
        ttsProjectRepository.save(ttsProject);
        em.flush();
        em.clear();

        //TTS detail 생성
        TTSDetail ttsDetail = TTSDetail.createTTSDetail(ttsProject, "unit Script test", 0);
        ttsDetailRepository.save(ttsDetail);
        em.flush();
        em.clear();

        //API Status 생성
        APIStatus beforeApiStatus = APIStatus.createAPIStatus(null, ttsDetail, "test");
        apiStatusRepository.save(beforeApiStatus);
        em.flush();
        em.clear();

        //DB에 저장한 APIStatus 객체 조회
        APIStatus afterApiStatus = apiStatusRepository.findById(beforeApiStatus.getId()).get();

        // 제대로 조회 됐는지 검증
        assertEquals(beforeApiStatus.getId(), afterApiStatus.getId());

        //when
            // 조회된 객체 삭제
        apiStatusRepository.delete(afterApiStatus);
        em.flush();
        em.clear();

        // then
            //DB에서 삭제된 apiStatus 정보에 대해 잘 삭제되었는지 확인
        APIStatus apiStatus = apiStatusRepository.findById(beforeApiStatus.getId()).orElse(null);
        assertNull(apiStatus);

    }
}
