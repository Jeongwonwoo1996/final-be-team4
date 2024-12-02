package com.fourformance.tts_vc_web.controller.common;

import com.fourformance.tts_vc_web.dto.response.ResponseDto;
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
     *
     * **사용법**:
     * - **URL**: `GET http://localhost:8080/sse/subscribe/{clientId}`
     * - `{clientId}`는 사용자 또는 프로젝트 식별 ID로 대체
     */
    @GetMapping("/subscribe/{clientId}")
    public SseEmitter registerClient(@PathVariable Long clientId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 타임아웃
        clients.put(clientId, emitter); // 클라이언트 저장

        // 연결 해제 시 클라이언트 제거
        emitter.onCompletion(() -> clients.remove(clientId)); // 연결 완료 시 제거
        emitter.onTimeout(() -> clients.remove(clientId)); // 타임아웃 발생 시 제거
        emitter.onError((e) -> clients.remove(clientId));  // 에러 발생 시 제거

        return emitter; // SSE 연결 객체 반환
    }

    /**
     * 작업 상태 변경 시 클라이언트에게 알림 전송
     * @param clientId 사용자 ID
     * @param data 전송할 데이터
     *
     * **사용법**:
     * - **기능**: 서버에서 작업 상태 업데이트 발생 시 연결된 클라이언트에게 전송
     * - **동작 방식**: 클라이언트가 `subscribe`로 연결되어 있어야 전송 가능
     * - 클라이언트가 연결되어 있지 않으면 전송 실패 처리
     */
    public void sendStatusUpdate(Long clientId, ResponseDto data) {
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
