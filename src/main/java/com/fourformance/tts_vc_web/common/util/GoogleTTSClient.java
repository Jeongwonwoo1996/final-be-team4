package com.fourformance.tts_vc_web.common.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.FileInputStream;
import java.io.IOException;

@Component
@Slf4j
public class GoogleTTSClient {

    @Value("${google.cloud.credentials.path}")
    private String credentialsPath;

    private TextToSpeechClient textToSpeechClient;

    /**
     * 애플리케이션 시작 시 TextToSpeechClient 초기화
     */
    @PostConstruct
    public void init() {
        try {
            log.info("GoogleTTSClient 초기화 시작: {}", credentialsPath);
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
            TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();
            textToSpeechClient = TextToSpeechClient.create(settings);
            log.info("GoogleTTSClient 초기화 완료");
        } catch (IOException e) {
            log.error("GoogleTTSClient 초기화 실패: {}", e.getMessage());
            throw new RuntimeException("Google TTS 클라이언트 초기화 실패", e);
        }
    }

    /**
     * TextToSpeechClient 반환
     *
     * @return TextToSpeechClient 인스턴스
     */
    public TextToSpeechClient getTextToSpeechClient() {
        return textToSpeechClient;
    }

    /**
     * 애플리케이션 종료 시 TextToSpeechClient 종료
     */
    @PreDestroy
    public void close() {
        if (textToSpeechClient != null) {
            textToSpeechClient.close();
            log.info("GoogleTTSClient 종료");
        }
    }
}
