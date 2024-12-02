package com.fourformance.tts_vc_web.common.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TaskStatusConst {
    NEW("새 작업"),
    RUNNABLE("진행중"),
    BLOCKED("일시정지"), //일시적으로 중단된 작업.
    WAITING("대기"),
    TERMINATED("종료"), //종료된 작업(강제 종료 포함).
    FAILED("실패"),
    COMPLETED("완료"); // 다중 작업 완료 상태 추가

    private final String descriptions;
}
