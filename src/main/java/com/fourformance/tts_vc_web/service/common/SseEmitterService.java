package com.fourformance.tts_vc_web.service.common;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.repository.ProjectRepository;
import com.fourformance.tts_vc_web.repository.common.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private static final Long DEFAULT_TIMEOUT = 30 * 60 * 1000L; // 30분
    private final EmitterRepository emitterRepository; // Emitter 저장소 인터페이스
    private final ProjectRepository projectRepository; // projectId로 memberId를 찾기 위한 Repository
    private final Logger log = Logger.getLogger(SseEmitterService.class.getName());

    /**
     * 클라이언트의 SSE 구독 요청을 처리
     */
    public SseEmitter subscribe(Long clientId) {
        log.info("Subscribing client with ID: " + clientId);
        SseEmitter emitter = createEmitter(clientId);

        // 초기 메시지 전송
        sendToClient(clientId, "Connection established for clientId: " + clientId);
        log.info("SSE subscription completed for clientId: " + clientId);

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
        });
        emitter.onTimeout(() -> {
            log.info("SSE Emitter timed out for clientId: " + clientId);
            emitterRepository.deleteById(clientId);
        });

        return emitter;
    }

    /**
     * 클라이언트에게 데이터 전송
     */
    public void sendToClient(Long memberId, Object data) {

        if (data == null) {
            data = "No data available";
        }

        if(memberId == null) { throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND); }


        SseEmitter emitter = emitterRepository.get(memberId);
        if (emitter != null) {
            try {
                log.info("Sending data to memberId: " + memberId);
                emitter.send(SseEmitter.event().id(String.valueOf(memberId)).name("taskUpdate").data(data));
            } catch (IOException exception) {
                log.warning("Failed to send data to memberId: " + memberId + ", removing emitter.");
                emitterRepository.deleteById(memberId);
                emitter.completeWithError(exception);
            }
        } else {
            log.warning("No active SSE connection for memberId: " + memberId);
        }
    }
}
