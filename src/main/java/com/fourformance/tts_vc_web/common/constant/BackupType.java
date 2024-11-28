package com.fourformance.tts_vc_web.common.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BackupType {
    TEMPORARY("임시백업"),
    FINAL("최종백업"),
    EXPIRED("만료된백업");

    private final String descriptions;
}
