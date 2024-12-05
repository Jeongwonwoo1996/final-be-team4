package com.fourformance.tts_vc_web.controller.common;

import com.fourformance.tts_vc_web.service.common.SseEmitterService;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SSEController {

    private final SseEmitterService sseEmitterService;

    /**
     * 클라이언트의 SSE 구독 요청을 처리
     *
     * @param clientId 클라이언트 ID
     * @return SseEmitter
     */
    @GetMapping("/subscribe/{clientId}")
    public SseEmitter subscribe(@PathVariable Long clientId) {
        return sseEmitterService.subscribe(clientId);
    }

    /**
     * 서버에서 특정 클라이언트로 데이터 전송 (디버그/테스트용)
     *
     * @param clientId 클라이언트 ID
     * @param message  전송할 메시지
     */
    @PostMapping("/send/{clientId}")
    public void sendMessageToClient(@PathVariable Long clientId, @RequestBody String message) {
        sseEmitterService.sendToClient(clientId, message);
    }

    @GetMapping("/test/{clientId}")
    public String testSSE(@PathVariable Long clientId) {
        return "테스트 SSE 응답: " + clientId;
    }
    //--------------------------------------------------------------------------------

    // 클라이언트별로 SseEmitter를 관리
    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam String clientId) {
        SseEmitter emitter = new SseEmitter();
        emitters.put(clientId, emitter); // clientId로 SseEmitter 저장

        emitter.onCompletion(() -> emitters.remove(clientId));
        emitter.onTimeout(() -> emitters.remove(clientId));
        emitter.onError((e) -> emitters.remove(clientId));

        return emitter;
    }

    // 연결 강제 종료
    @PostMapping("/disconnect")
    public ResponseEntity<String> disconnect(@RequestParam String clientId) {
        SseEmitter emitter = emitters.remove(clientId);
        if (emitter != null) {
            emitter.complete(); // 연결 강제 종료
            return ResponseEntity.ok("Disconnected client: " + clientId);
        }
        return ResponseEntity.badRequest().body("No active connection for client: " + clientId);
    }

    @PostMapping("/disconnect-all")
    public ResponseEntity<String> disconnectAll() {
        for (SseEmitter emitter : emitters.values()) {
            emitter.complete(); // 모든 연결 강제 종료
        }
        emitters.clear(); // 리스트 초기화
        return ResponseEntity.ok("All connections disconnected");
    }

    @GetMapping("/active-connections")
    public ResponseEntity<Integer> activeConnections() {
        return ResponseEntity.ok(emitters.size()); // 현재 연결 수 반환
    }

}