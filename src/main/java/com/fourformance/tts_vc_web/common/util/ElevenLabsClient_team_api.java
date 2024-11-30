package com.fourformance.tts_vc_web.common.util;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.logging.Logger;

import static org.hibernate.sql.results.LoadingLogger.LOGGER;

@Component
public class ElevenLabsClient_team_api {

    @Value("${elevenlabs.api.url}")
    private String baseUrl;

    @Value("${elevenlabs.api.key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();

    /**
     * 타겟 오디오 파일을 업로드하여 Voice ID를 생성합니다.
     *
     * @param targetAudioPath S3 URL 또는 로컬 파일 경로
     * @return 생성된 Voice ID
     * @throws IOException 파일 처리 중 오류
     */
    public String uploadVoice(String targetAudioPath) throws IOException {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", "user_custom_voice")
                .addFormDataPart("remove_background_noise", "false");

        RequestBody audioRequestBody;
        String fileName;

        if (targetAudioPath.startsWith("http:/") || targetAudioPath.startsWith("https:/")) {
            // S3 URL에서 파일 다운로드
            URL url = new URL(targetAudioPath);
            try (InputStream inputStream = url.openStream()) {
                byte[] audioBytes = inputStream.readAllBytes();
                fileName = Paths.get(url.getPath()).getFileName().toString();
                audioRequestBody = RequestBody.create(audioBytes, MediaType.parse("audio/mpeg"));
            }
        } else {
            throw new IllegalArgumentException("지원하지 않는 파일 경로입니다: " + targetAudioPath);
        }

        builder.addFormDataPart("files", fileName, audioRequestBody);

        Request request = new Request.Builder()
                .url(baseUrl + "/voices/add")
                .addHeader("xi-api-key", apiKey)
                .post(builder.build())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Voice ID 생성 실패: " + response.body().string());
            }

            String responseBody = response.body().string();
            return extractVoiceId(responseBody);
        }
    }

    /**
     * Voice ID와 소스 오디오 파일을 사용하여 음성을 변환합니다.
     *
     * @param voiceId        생성된 Voice ID
     * @param audioFilePath  소스 오디오의 S3 URL
     * @return 변환된 파일의 경로
     * @throws IOException 변환 중 오류
     */
    public String convertSpeechToSpeech(String voiceId, String audioFilePath) throws IOException {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("model_id", "eleven_english_sts_v2")
                .addFormDataPart("remove_background_noise", "false");

        if (audioFilePath.startsWith("http:/") || audioFilePath.startsWith("https:/")) {
            // S3 URL에서 파일 다운로드 및 폼 데이터 추가
            URL url = new URL(audioFilePath);
            try (InputStream inputStream = url.openStream()) {
                byte[] audioBytes = inputStream.readAllBytes();
                builder.addFormDataPart("audio", "source.mp3",
                        RequestBody.create(audioBytes, MediaType.parse("audio/mpeg")));
            }
        } else {
            throw new IllegalArgumentException("지원하지 않는 파일 경로입니다: " + audioFilePath);
        }

        Request request = new Request.Builder()
                .url(baseUrl + "/speech-to-speech/" + voiceId)
                .addHeader("xi-api-key", apiKey)
                .post(builder.build())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("변환 실패: " + response.body().string());
            }

            // 변환된 파일 저장 경로 결정
            File tempFile = File.createTempFile("vc_audio_", ".mp3");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(response.body().bytes());
            }

            LOGGER.info("[파일 변환 완료] 파일 경로: " + tempFile.getAbsolutePath());

            return tempFile.getAbsolutePath();
        }
    }

    /**
     * Eleven Labs API 응답에서 Voice ID를 추출합니다.
     *
     * @param responseBody API 응답 본문
     * @return 추출된 Voice ID
     */
    private String extractVoiceId(String responseBody) {
        String prefix = "voice_id\":\"";
        int startIndex = responseBody.indexOf(prefix) + prefix.length();
        int endIndex = responseBody.indexOf("\"", startIndex);
        return responseBody.substring(startIndex, endIndex);
    }
}