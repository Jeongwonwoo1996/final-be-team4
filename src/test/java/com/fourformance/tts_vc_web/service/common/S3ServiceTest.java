package com.fourformance.tts_vc_web.service.common;

import com.fourformance.tts_vc_web.repository.ConcatProjectRepository;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import com.fourformance.tts_vc_web.repository.TTSDetailRepository;
import com.fourformance.tts_vc_web.repository.TTSProjectRepository;
import com.fourformance.tts_vc_web.repository.VCDetailRepository;
import com.fourformance.tts_vc_web.repository.VCProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
//@Rollback(false)
class S3ServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TTSProjectRepository ttsProjectRepository;
    @Autowired
    private TTSDetailRepository ttsDetailRepository;
    @Autowired
    private ConcatProjectRepository concatProjectRepository;
    @Autowired
    private VCProjectRepository vcProjectRepository;
    @Autowired
    private VCDetailRepository vcDetailRepository;

//    @Test
//    public void 테스트용_TTS_디테일_생성() {
//        Member member = Member.createMember("abc@abc.com", "abc123", "철수", 0, now(), "010123123");
//        TTSProject ttsProject = TTSProject.createTTSProject(member, "테스트 tts 프로젝트", null, null, null, null, null);
//        TTSDetail ttsDetail = TTSDetail.createTTSDetail(ttsProject, "테스트 tts 스크립트", 1);
//
//        memberRepository.save(member);
//        ttsProjectRepository.save(ttsProject);
//        ttsDetailRepository.save(ttsDetail);
//    }
//
//    @Test
//    public void 테스트용_TTS_더미_날리기() {
//        ttsDetailRepository.deleteAll();
//        ttsProjectRepository.deleteAll();
//        memberRepository.deleteAll();
//    }
//
//    @Test
//    public void 테스트용_VC_디테일_생성() {
//        Member member = Member.createMember("abc@abc.com", "abc123", "철수", 0, now(), "010123123");
//
//        VCProject vcProject = VCProject.createVCProject(member, "테스트 vc 프로젝트");
//        VCDetail vcDetail = VCDetail.createVCDetail(vcProject, null);
//
//        memberRepository.save(member);
//        vcProjectRepository.save(vcProject);
//        vcDetailRepository.save(vcDetail);
//    }
//
//    @Test
//    public void 테스트용_VC_더미_날리기() {
//        vcDetailRepository.deleteAll();
//        vcProjectRepository.deleteAll();
//        memberRepository.deleteAll();
//    }
//
//    @Test
//    public void 테스트용_컨캣_프로젝트_생성() {
//        Member member = Member.createMember("abc@abc.com", "abc123", "짱구", 0, now(), "010123123");
//        ConcatProject concatProject = ConcatProject.createConcatProject(member, "테스트 컨캣 프로젝트");
//
//        memberRepository.save(member);
//        concatProjectRepository.save(concatProject);
//    }
//
//    @Test
//    public void 테스트용_컨캣_더미_날리기() {
//        ttsDetailRepository.deleteAll();
//        ttsProjectRepository.deleteAll();
//        memberRepository.deleteAll();
//    }
//
////    @Test
////    public void 프라사인드_테스트용_더미_생성() {
////        Member member = Member.createMember("abc@abc.com", "abc123", "철수",0,now(), "010123123");
////        TTSProject ttsProject = TTSProject.createTTSProject(member, "테스트 tts 프로젝트");
////        TTSDetail ttsDetail = TTSDetail.createTTSDetail(ttsProject, "테스트 tts 스크립트", 1);
////
////        memberRepository.save(member); // memberId = 1;
////        ttsProjectRepository.save(ttsProject); // projectId = 1;
////        ttsDetailRepository.save(ttsDetail); // ttsDetailId = 1;
////    }
//
//    @Test
//    public void 프리사인드_더미_날리기() {
//        ttsDetailRepository.deleteAll();
//        ttsProjectRepository.deleteAll();
//        memberRepository.deleteAll();
//    }


}