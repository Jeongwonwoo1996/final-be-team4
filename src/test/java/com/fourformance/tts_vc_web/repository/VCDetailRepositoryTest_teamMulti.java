//package com.fourformance.tts_vc_web.repository;
//
//import com.fourformance.tts_vc_web.common.constant.AudioType;
//import com.fourformance.tts_vc_web.domain.entity.Member;
//import com.fourformance.tts_vc_web.domain.entity.MemberAudioMeta;
//import com.fourformance.tts_vc_web.domain.entity.VCDetail;
//import com.fourformance.tts_vc_web.domain.entity.VCProject;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.Random;
//
//@SpringBootTest
//@Transactional //테스트 클래스에 적용되면 각 테스트가 독립적인 트랜잭션 내에서 실행
////@Rollback(false) //롤백 여부를 제어
//class VCDetailRepositoryTest_teamMulti {
//
//    @Autowired
//    VCDetailRepository vcDetailRepository;
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Autowired
//    VCProjectRepository vcProjectRepository;
//
//    @Autowired
//    MemberAudioMetaRepository memberAudioMetaRepository;
//    @Autowired
//    private OutputAudioMetaRepository outputAudioMetaRepository;
//
//
//    //member 생성
//    public Member createMember() {
//        return Member.createMember("test2@email.com", "12345", "오길동", 0, LocalDateTime.now(), "01012345678",true);
//    }
//
//    // VCProject 생성
//    public VCProject createVCProject(Member member) {
//        return VCProject.createVCProject(member, "vc프로젝트");
//    }
//
//    // MemberAudioMeta 생성
//    public MemberAudioMeta createMemberAudioMeta(Member member) {
//        return MemberAudioMeta.createMemberAudioMeta(member,null,"url_test", AudioType.VC_SRC);
//    }
//
//    // MemberAudioMeta 20개 생성
//    public List<MemberAudioMeta> createMemberAudioMetas(Member member) {
//        List<MemberAudioMeta> audioMetaList = new ArrayList<>();
//        AudioType[] audioTypes = AudioType.values(); // AudioType의 모든 값 가져오기
//        Random random = new Random();
//
//        for (int i = 1; i <= 20; i++) {
//            // AudioType 중 하나를 무작위로 선택
//            AudioType randomAudioType = audioTypes[random.nextInt(audioTypes.length)];
//
//            MemberAudioMeta memberAudioMeta = MemberAudioMeta.createMemberAudioMeta(
//                    member, null,"url_test_" + i, randomAudioType); // 랜덤 AudioType 사용
//            audioMetaList.add(memberAudioMetaRepository.save(memberAudioMeta)); // 저장 후 리스트에 추가
//        }
//        return audioMetaList;
//    }
//
//    @Test
//    @DisplayName("VCDetail 저장 테스트")
//    public void testCreateVCDetail() {
//        // 사전 작업
//        // Member,VCProject,MemberAudioMeta 생성 및 저장
//        Member savedMember = memberRepository.save(createMember());
//        VCProject savedProject = vcProjectRepository.save(createVCProject(savedMember));
//        MemberAudioMeta savedAudioMeta = memberAudioMetaRepository.save(createMemberAudioMeta(savedMember));
//
//        // VCDetail 생성 및 저장
//        VCDetail vcDetail = VCDetail.createVCDetail(savedProject, savedAudioMeta);
//        VCDetail savedDetail = vcDetailRepository.save(vcDetail);
//
//        // 저장 확인
//        Assertions.assertThat(savedDetail.getId()).isNotNull();
//        Assertions.assertThat(savedDetail.getVcProject()).isEqualTo(savedProject);
//        Assertions.assertThat(savedDetail.getMemberAudioMeta()).isEqualTo(savedAudioMeta);
//    }
//
//    @Test
//    @DisplayName("VCDetail 20개 저장 테스트")
//    public void testCreateMultipleVCDetails() {
//        // 사전 작업
//        Member savedMember = memberRepository.save(createMember());
//        VCProject savedProject = vcProjectRepository.save(createVCProject(savedMember));
//
//        // 20개의 MemberAudioMeta 생성
//        List<MemberAudioMeta> audioMetaList = createMemberAudioMetas(savedMember);
//
//        // VCDetail 20개 생성 및 저장
//        for (int i = 0; i < 20; i++) {
//            VCDetail vcDetail = VCDetail.createVCDetail(savedProject, audioMetaList.get(i));
//            vcDetail.updateDetails(false, "UnitScript " + (i + 1)); // 고유한 unitScript 설정
//            vcDetailRepository.save(vcDetail);
//
//            Assertions.assertThat(vcDetail.getId()).isNotNull(); // 저장 확인
//        }
//    }
//
//    @Test
//    @DisplayName("VCDetail 단건 조회 테스트")
//    public void testFindVCDetail() {
//        // 사전 작업: VCDetail 데이터 저장
//        Member savedMember = memberRepository.save(createMember());
//        VCProject savedProject = vcProjectRepository.save(createVCProject(savedMember));
//        MemberAudioMeta savedAudioMeta = memberAudioMetaRepository.save(createMemberAudioMeta(savedMember));
//        VCDetail savedDetail = vcDetailRepository.save(VCDetail.createVCDetail(savedProject, savedAudioMeta));
//
//        // ID로 조회
//        Optional<VCDetail> foundDetail = vcDetailRepository.findById(savedDetail.getId());
//        Assertions.assertThat(foundDetail).isPresent();
//        Assertions.assertThat(foundDetail.get().getVcProject()).isEqualTo(savedProject);
//        Assertions.assertThat(foundDetail.get().getMemberAudioMeta()).isEqualTo(savedAudioMeta);
//    }
//
//    @Test
//    @DisplayName("VCDetail 여러 건 조회 테스트")
//    public void testFindMultipleVCDetails() {
//        // 사전 작업: Member, VCProject 생성
//        Member savedMember = memberRepository.save(createMember());
//        VCProject savedProject = vcProjectRepository.save(createVCProject(savedMember));
//
//        outputAudioMetaRepository.deleteAll();
//        vcDetailRepository.deleteAll();
//
//        // 여러 개의 VCDetail 저장 (10개 생성 예시)
//        List<VCDetail> vcDetails = new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            MemberAudioMeta savedAudioMeta = memberAudioMetaRepository.save(createMemberAudioMeta(savedMember));
//            VCDetail vcDetail = VCDetail.createVCDetail(savedProject, savedAudioMeta);
//            vcDetail.updateDetails(false, "UnitScript " + i);
//            vcDetails.add(vcDetailRepository.save(vcDetail));
//        }
//
//        // 전체 조회
//        List<VCDetail> foundDetails = vcDetailRepository.findAll();
//
//        // 저장된 VCDetail 수와 조회된 수가 일치하는지 확인
//        Assertions.assertThat(foundDetails.size()).isEqualTo(vcDetails.size());
//        Assertions.assertThat(foundDetails).containsAll(vcDetails); // 저장한 모든 VCDetail이 조회되었는지 확인
//    }
//
//
//    @Test
//    @DisplayName("VCDetail 업데이트 테스트")
//    public void testUpdateVCDetail() {
//        // 사전 작업: VCDetail 데이터 저장
//        Member savedMember = memberRepository.save(createMember());
//        VCProject savedProject = vcProjectRepository.save(createVCProject(savedMember));
//        MemberAudioMeta savedAudioMeta = memberAudioMetaRepository.save(createMemberAudioMeta(savedMember));
//        VCDetail savedDetail = vcDetailRepository.save(VCDetail.createVCDetail(savedProject, savedAudioMeta));
//
//        // 업데이트: isChecked와 unitScript 업데이트
//        savedDetail.updateDetails(true, "Updated UnitScript");
//        vcDetailRepository.save(savedDetail);
//
//        // 업데이트 후 확인
//        Optional<VCDetail> updatedDetail = vcDetailRepository.findById(savedDetail.getId());
//        Assertions.assertThat(updatedDetail).isPresent();
//        Assertions.assertThat(updatedDetail.get().getIsChecked()).isTrue();
//        Assertions.assertThat(updatedDetail.get().getUnitScript()).isEqualTo("Updated UnitScript");
//    }
//
//    @Test
//    @DisplayName("VCDetail 여러 건 업데이트 테스트")
//    public void testUpdateMultipleVCDetails() {
//        // 사전 작업: Member, VCProject 생성
//        Member savedMember = memberRepository.save(createMember());
//        VCProject savedProject = vcProjectRepository.save(createVCProject(savedMember));
//
//        outputAudioMetaRepository.deleteAll();
//        vcDetailRepository.deleteAll();
//
//        // 여러 개의 VCDetail 저장 (10개 생성 예시)
//        List<VCDetail> vcDetails = new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            MemberAudioMeta savedAudioMeta = memberAudioMetaRepository.save(createMemberAudioMeta(savedMember));
//            VCDetail vcDetail = VCDetail.createVCDetail(savedProject, savedAudioMeta);
//            vcDetail.updateDetails(false, "UnitScript " + i);
//            vcDetails.add(vcDetailRepository.save(vcDetail));
//        }
//
//        // 저장된 VCDetail 전체를 업데이트
//        vcDetails.forEach(vcDetail -> {
//            vcDetail.updateDetails(true, "Updated UnitScript");
//            vcDetailRepository.save(vcDetail); // 업데이트 후 저장
//        });
//
//        // 업데이트 후 검증
//        List<VCDetail> updatedDetails = vcDetailRepository.findAll();
//        Assertions.assertThat(updatedDetails.size()).isEqualTo(vcDetails.size());
//
//        // 모든 VCDetail이 업데이트된 내용으로 반영되었는지 확인
//        updatedDetails.forEach(updatedDetail -> {
//            Assertions.assertThat(updatedDetail.getIsChecked()).isTrue();
//            Assertions.assertThat(updatedDetail.getUnitScript()).isEqualTo("Updated UnitScript");
//        });
//    }
//
//
//    @Test
//    @DisplayName("VCDetail 삭제 테스트")
//    public void testDeleteVCDetail() {
//        // 사전 작업: VCDetail 데이터 저장
//        Member savedMember = memberRepository.save(createMember());
//        VCProject savedProject = vcProjectRepository.save(createVCProject(savedMember));
//        MemberAudioMeta savedAudioMeta = memberAudioMetaRepository.save(createMemberAudioMeta(savedMember));
//        VCDetail savedDetail = vcDetailRepository.save(VCDetail.createVCDetail(savedProject, savedAudioMeta));
//
//        // 삭제
//        vcDetailRepository.delete(savedDetail);
//
//        // 삭제 후 확인: 조회 시 존재하지 않아야 함
//        Optional<VCDetail> deletedDetail = vcDetailRepository.findById(savedDetail.getId());
//        Assertions.assertThat(deletedDetail).isNotPresent();
//    }
//}
