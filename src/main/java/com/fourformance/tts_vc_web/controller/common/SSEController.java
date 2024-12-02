package com.fourformance.tts_vc_web.controller.common;

import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.dto.tts.TTSResponseDetailDto;
import com.fourformance.tts_vc_web.service.common.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SSEController {

    private final SseEmitterService sseEmitterService;

    /**
     * 클라이언트의 SSE 구독 요청을 처리
     * @param clientId 클라이언트 ID
     * @return SseEmitter
     */
    @GetMapping("/subscribe/{clientId}")
    public SseEmitter subscribe(@PathVariable Long clientId) {
        return sseEmitterService.subscribe(clientId);
    }

    /**
     * 서버에서 특정 클라이언트로 데이터 전송 (디버그/테스트용)
     * @param clientId 클라이언트 ID
     * @param message 전송할 메시지
     */
    @PostMapping("/send/{clientId}")
    public void sendMessageToClient(@PathVariable Long clientId, @RequestBody String message) {
        sseEmitterService.sendToClient(clientId, message);
    }

    @GetMapping("/test/{clientId}")
    public String testSSE(@PathVariable Long clientId) {
        return "테스트 SSE 응답: " + clientId;
    }
}
