package com.fourformance.tts_vc_web.controller.common;

import com.fourformance.tts_vc_web.dto.tts.TTSResponseDetailDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/sse")
public class SSEController {

    // 클라이언트를 관리하기 위한 SseEmitter 저장소
    private final ConcurrentHashMap<Long, SseEmitter> clients = new ConcurrentHashMap<>();

    /**
     * 클라이언트가 SSE 연결을 생성
     * @param clientId 사용자 ID (로그인 사용자)
     * @return SseEmitter
     */
    @GetMapping("/subscribe/{clientId}")
    public SseEmitter registerClient(@PathVariable Long clientId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 타임아웃
        clients.put(clientId, emitter);

        // 연결 해제 시 클라이언트 제거
        emitter.onCompletion(() -> clients.remove(clientId));
        emitter.onTimeout(() -> clients.remove(clientId));
        emitter.onError((e) -> clients.remove(clientId));

        return emitter;
    }

    /**
     * 작업 상태 변경 시 클라이언트에게 알림 전송
     * @param clientId 사용자 ID
     * @param data 전송할 데이터
     */
    public void sendStatusUpdate(Long clientId, TTSResponseDetailDto data) {
        SseEmitter emitter = clients.get(clientId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("taskUpdate")
                        .data(data));
            } catch (IOException e) {
                clients.remove(clientId); // 전송 실패 시 제거
            }
        }
    }
}
