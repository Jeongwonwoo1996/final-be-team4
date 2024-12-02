package com.fourformance.tts_vc_web.common.scheduler;

import com.fourformance.tts_vc_web.service.common.MemberAudioMetaService;
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

    // 한 달 지난 vc_trg 오디오와 vc_src 오디오 버킷에서 삭제 및 db 업데이트
    @Scheduled(cron = "0 0 0 * * ?") // 매일 00시 실행
    public void scheduleVcInput() {
        memberAudioMetaService.deleteOldVcAudios();
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
