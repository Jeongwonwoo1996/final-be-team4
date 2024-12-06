package com.fourformance.tts_vc_web.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해 적용
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://127.0.0.1:5173",
                        "http://172.*.*.*/5173",
                        "https://dev.popomance.kr",
                        "https://www.popomance.kr",
                        "https://popomance.kr",
                        "https://*.web.app",
                        "https://aipark-four-t--preview-mfbtwo6o.web.app",
                        "https://aipark-four-t.web.app",
                        "https://aipark-four-t.firebaseapp.com"
                ) // 허용할 도메인
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 인증 정보 허용
                .maxAge(3600); // Preflight 요청 캐시 시간
    }
}