//package com.fourformance.tts_vc_web.repository;
//
//import com.fourformance.tts_vc_web.domain.entity.*;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import com.fourformance.tts_vc_web.common.constant.TaskStatusConst;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@SpringBootTest
//@Transactional //테스트 클래스에 적용되면 각 테스트가 독립적인 트랜잭션 내에서 실행
////@Rollback(false) //롤백 여부를 제어
//class MultiJobLogRepositoryTest {
//    @Autowired
//    private MultiJobLogRepository multiJobLogRepository;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private VCProjectRepository vcProjectRepository;
//
//    //member 생성
//    public Member createMember() {
//        return Member.createMember("test2@email.com", "12345", "오길동", 0, LocalDateTime.now(), "01012345678");
//    }
//    // VCProject 생성
//    public VCProject createVCProject(Member member) {
//        return VCProject.createVCProject(member, "vc다중탭작업");
//    }
//    // MultiJobLog 생성 메서드
//    public MultiJobLog createMultiJobLog(Project project) {
//        return MultiJobLog.createMultiJobLog(
//                project, TaskStatusConst.WAITING, "TestFailBy", "TestComment", 1);
//    }
//    // MultiJobLog 생성 메서드
//    public MultiJobLog createMultiJobLog(Project project, TaskStatusConst statusConst) {
//        return MultiJobLog.createMultiJobLog(
//                project, statusConst, "TestFailBy", "TestComment", 1);
//    }
//
//    @Test
//    @DisplayName("다중작업 이력 생성")
//    public void createTest(){
//        //사전 작업
//        Member saveMember = memberRepository.save(createMember());
//        VCProject saveVCProject = vcProjectRepository.save(createVCProject(saveMember));
//
//        MultiJobLog savedMultiJobLog = multiJobLogRepository.save(createMultiJobLog(saveVCProject));
//
//        // 저장 확인
//        Assertions.assertThat(savedMultiJobLog.getId()).isNotNull();
//        Assertions.assertThat(savedMultiJobLog.getProject().getProjectName()).isEqualTo("vc다중탭작업");
//        Assertions.assertThat(savedMultiJobLog.getTaskStatusConst()).isEqualTo(TaskStatusConst.WAITING);
//
//    }
//
//    @Test
//    @DisplayName("다중작업 이력 조회 테스트")
//    public void findTest() {
//        // 사전 작업
//        Member savedMember = memberRepository.save(createMember());
//        VCProject savedVCProject = vcProjectRepository.save(createVCProject(savedMember));
//
//        // 다중 작업 이력 생성 및 저장
//        MultiJobLog savedMultiJobLog1 = multiJobLogRepository.save(createMultiJobLog(savedVCProject, TaskStatusConst.WAITING));
//        MultiJobLog savedMultiJobLog2 = multiJobLogRepository.save(createMultiJobLog(savedVCProject, TaskStatusConst.BLOCKED));
//
//        // 특정 Project ID로 조회
//        List<MultiJobLog> jobLogs = multiJobLogRepository.findAllByProjectId(savedVCProject.getId());
//
//        // 조회 확인
//        Assertions.assertThat(jobLogs).isNotEmpty();
//        Assertions.assertThat(jobLogs).hasSize(2);
//        Assertions.assertThat(jobLogs).extracting("multiJobLogStatusConst")
//                .containsExactly(TaskStatusConst.WAITING, TaskStatusConst.BLOCKED);
//    }
//
//    @Test
//    @DisplayName("다중작업 이력 상태 변경 테스트") // 이력이라서 굳이 상태 변경 안해도 되지만 일단 함
//    public void statusChangeTest() {
//        // 사전 작업
//        Member savedMember = memberRepository.save(createMember());
//        VCProject savedVCProject = vcProjectRepository.save(createVCProject(savedMember));
//
//        // 상태 변경 테스트: 처음에는 WAITING 상태로 저장
//        MultiJobLog initialJobLog = multiJobLogRepository.save(createMultiJobLog(savedVCProject, TaskStatusConst.WAITING));
//
//        // 상태 변경: 새로운 상태로 저장 (NEW)
//        MultiJobLog inProgressJobLog = multiJobLogRepository.save(createMultiJobLog(savedVCProject, TaskStatusConst.NEW));
//
//        // 상태 변경: 완료 상태로 저장 (TERMINATED)
//        MultiJobLog completedJobLog = multiJobLogRepository.save(createMultiJobLog(savedVCProject, TaskStatusConst.TERMINATED));
//
//        // 각 상태가 이력으로 남았는지 확인
//        List<MultiJobLog> jobLogs = multiJobLogRepository.findAllByProjectId(savedVCProject.getId());
//
//        // 상태 이력 확인
//        Assertions.assertThat(jobLogs).hasSize(3);
//        Assertions.assertThat(jobLogs).extracting("multiJobLogStatusConst")
//                .containsExactly(TaskStatusConst.WAITING, TaskStatusConst.NEW, TaskStatusConst.TERMINATED);
//    }
//
//
//}