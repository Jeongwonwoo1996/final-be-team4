package com.fourformance.tts_vc_web.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public abstract class ResponseDto {

    private final Boolean success;
    private final Integer code;
    private final String message;
}