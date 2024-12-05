package com.fourformance.tts_vc_web.repository.common;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EmitterRepository {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void save(Long clientId, SseEmitter emitter) {
        emitters.put(clientId, emitter);
    }

    public SseEmitter get(Long clientId) {
        return emitters.get(clientId);
    }

    public void deleteById(Long clientId) {
        emitters.remove(clientId);
    }

    public void deleteAll() {emitters.clear();}

    public int count() {return emitters.size();}

}
