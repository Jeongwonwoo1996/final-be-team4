package com.fourformance.tts_vc_web.controller.common;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.dto.common.InitResDto;
import com.fourformance.tts_vc_web.dto.common.VoiceStyleDto;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.service.common.VoiceStyleService_team_multi;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController { /* 초기 API 요청 시 반환되는 데이터 값 */

    private final VoiceStyleService_team_multi voiceStyle;

    @GetMapping("/")
    public ResponseDto start() {
        return DataResponseDto.of("", "안녕하세요");
    }

    @GetMapping("voice-style")
    public ResponseDto voice() {

        // Voice Style DB 데이터 반환
        List<VoiceStyleDto> voiceStyleList = voiceStyle.getVisibleVoiceStyles();

        if (voiceStyleList == null || voiceStyleList.isEmpty()) {
            throw new BusinessException(ErrorCode.VOICE_STYLE_NOT_FOUND_EXCEPTION);
        }

        InitResDto response = new InitResDto(voiceStyleList);

        return DataResponseDto.of(response, "서버 초기 API 요청 성공");
    }


}
