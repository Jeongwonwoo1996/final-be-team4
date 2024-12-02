package com.fourformance.tts_vc_web.controller.common;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseClientController {
    private final SSEController sseController;

    @GetMapping("/sse/register/{clientId}")
    public SseEmitter register(@PathVariable Long clientId) {
        return sseController.registerClient(clientId);
    }
}
