package com.fourformance.tts_vc_web.service.common;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import com.fourformance.tts_vc_web.repository.ProjectRepository;
import com.fourformance.tts_vc_web.repository.common.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private static final Long DEFAULT_TIMEOUT = 5 * 60 * 1000L; // 5분
    private static final Long INACTIVITY_TIMEOUT = 1 * 60 * 1000L; // 1분 (유휴 시간)
    private final EmitterRepository emitterRepository; // Emitter 저장소 인터페이스
    private final Logger log = Logger.getLogger(SseEmitterService.class.getName());

    private final MemberRepository memberRepository;

    // 클라이언트 마지막 작업 시간 추적
    private final Map<Long, Long> lastActivityMap = new ConcurrentHashMap<>();

    /**
     * 클라이언트의 SSE 구독 요청을 처리
     */
    public SseEmitter subscribe(Long clientId) {
        log.info("Validating client ID: " + clientId);

        // 유효성 검증: clientId가 Member 테이블에 존재하는지 확인
        if (!memberRepository.existsById(clientId)) {
            log.warning("Invalid clientId: " + clientId + " - Member not found");
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND); // 적절한 예외 반환
        }

        log.info("Subscribing client with ID: " + clientId);
        SseEmitter emitter = createEmitter(clientId);

        try {
            // 초기 메시지 전송
            emitter.send(SseEmitter.event()
                    .id(String.valueOf(clientId))
                    .name("connection")
                    .data("Connection established for clientId: " + clientId));
            log.info("SSE subscription completed for clientId: " + clientId);

            updateLastActivity(clientId); // 초기 작업 시간 갱신
        } catch (IOException exception) {
            log.warning("Failed to send initial message to clientId: " + clientId);
            emitterRepository.deleteById(clientId); // 초기 전송 실패 시 Emitter 삭제
        }

        return emitter;
    }

    /**
     * SSE Emitter 생성 및 저장
     */
    private SseEmitter createEmitter(Long clientId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(clientId, emitter);
        log.info("Created SSE Emitter for clientId: " + clientId);

        // Emitter 완료/타임아웃 처리
        emitter.onCompletion(() -> {
            log.info("SSE Emitter completed for clientId: " + clientId);
            emitterRepository.deleteById(clientId);
            lastActivityMap.remove(clientId);
        });
        emitter.onTimeout(() -> {
            log.info("SSE Emitter timed out for clientId: " + clientId);
            emitterRepository.deleteById(clientId);
            lastActivityMap.remove(clientId);
        });
        emitter.onError(e -> {
            log.warning("Error in SSE Emitter for clientId: " + clientId + ", removing emitter.");
            emitterRepository.deleteById(clientId);
            lastActivityMap.remove(clientId);
        });

        return emitter;
    }

    /**
     * 클라이언트에게 데이터 전송
     */
    public void sendToClient(Long clientId, Object data) {
        if (clientId == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        if (data == null) {
            data = "서버에서 넘어온 데이터가 없습니다.";
        }

        SseEmitter emitter = emitterRepository.get(clientId);
        if (emitter != null) {
            try {
                log.info("Sending data to clientId: " + clientId);
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(clientId))
                        .name("taskUpdate")
                        .data(data));
                lastActivityMap.remove(clientId); // 데이터 전송 시 활동 시간 갱신
            } catch (IOException exception) {
                log.warning("Failed to send data to clientId: " + clientId + ", removing emitter.");
                emitterRepository.deleteById(clientId);
                lastActivityMap.remove(clientId);
                emitter.completeWithError(exception);
            }
        } else {
            log.warning("No active SSE connection for clientId: " + clientId);
        }
    }

    /**
     * 특정 클라이언트 연결 강제 종료
     */
    public void disconnect(Long clientId) {
        SseEmitter emitter = emitterRepository.get(clientId);

        if (emitter != null) {
            long lastActivityTime = lastActivityMap.getOrDefault(clientId, 0L);
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastActivityTime > INACTIVITY_TIMEOUT) {
                log.info("ClientId: " + clientId + " exceeded inactivity timeout. Disconnecting.");

                emitter.complete();

                emitterRepository.deleteById(clientId);
                lastActivityMap.remove(clientId);

                log.info("Disconnected clientId: " + clientId + " due to inactivity.");
            } else {
                log.info("ClientId: " + clientId + " is still active.");
            }
        } else {
            log.warning("No active SSE connection for clientId: " + clientId);
        }
    }

    /**
     * 모든 클라이언트 연결 강제 종료
     */
    public void disconnectAll() {
        emitterRepository.deleteAll();
        lastActivityMap.clear();
        log.info("All SSE connections have been disconnected");
    }

    /**
     * 현재 활성화된 연결 수 반환
     */
    public int getActiveConnectionsCount() {
        return emitterRepository.count();
    }

    /**
     * 마지막 작업 시간 갱신
     */
    private void updateLastActivity(Long clientId) {
        lastActivityMap.put(clientId, System.currentTimeMillis());
        log.info("Updated last activity for clientId: " + clientId);
    }
}
