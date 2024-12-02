package com.fourformance.tts_vc_web.common.scheduler;

import com.fourformance.tts_vc_web.service.common.MemberAudioMetaService;
import com.fourformance.tts_vc_web.service.common.SchedulerService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class AudioScheduler {

    private final MemberAudioMetaService memberAudioMetaService;
    private final SchedulerService schedulerService;

    // 한 달 지난 vc_trg 오디오와 vc_src 오디오 버킷에서 삭제 및 db 업데이트
    @Scheduled(cron = "0 0 0 * * ?") // 매일 00시 실행
    public void scheduleVcInput() {
        memberAudioMetaService.deleteOldVcAudios();
    }

    @Scheduled(cron = "0 1 19 * * ?") // 매주 일요일 새벽 1시에 처리 예약
    public void cleanUpDeletedFiles() {
        System.out.println(
                "지정된 시간입니다!!!!! concat 데이터 전부 날리겠습니다!!!! @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("S3에서 삭제 요청된 파일을 확인하고 삭제 작업을 시작합니다...");
        schedulerService.recheckDeleteAllS3Audio();
        System.out.println("S3에서 불필요한 파일 삭제 작업을 완료했습니다.");
    }

//    @Scheduled(cron = "0 28 16 * * ?") // 매일 오후 4시 10분
//    public void scheduleVcInputTest() {
//        memberAudioMetaService.deleteOldVcAudiosTest();
//    }

    // 테스트 메서드: 10초마다 로그 출력
    @Scheduled(fixedRate = 10000) // 10초마다 실행
    public void testScheduler() {
        System.out.println("스케줄러 잘 동작하는지 확인중 현재 시간 : " + LocalDateTime.now());
    }

}
