package com.fourformance.tts_vc_web.controller.common;

import com.fourformance.tts_vc_web.service.common.SchedulerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AudioCleanUpScheduler {

    private final SchedulerService schedulerService;

    public AudioCleanUpScheduler(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @Scheduled(cron = "0 20 18 * * 0") // 매주 일요일 새벽 1시에 처리 예약
    public void cleanUpDeletedFiles() {
        System.out.println("S3에서 삭제 요청된 파일을 확인하고 삭제 작업을 시작합니다...");
        schedulerService.recheckDeleteAllS3Audio();
        System.out.println("S3에서 불필요한 파일 삭제 작업을 완료했습니다.");


    }
}
