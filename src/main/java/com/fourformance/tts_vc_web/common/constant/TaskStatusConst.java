package com.fourformance.tts_vc_web.common.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TaskStatusConst {
    NEW("..."),
    RUNNABLE("진행중"),
    BLOCKED("일시정지"),
    WAITING("대기"),
    TERMINATED("종료");

    private final String descriptions;
}
