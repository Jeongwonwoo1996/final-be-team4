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
        RequestBody audioRequestBody = createAudioRequestBody(targetAudioPath);
        String fileName = extractFileName(targetAudioPath);

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", "user_custom_voice")
                .addFormDataPart("remove_background_noise", "false")
                .addFormDataPart("files", fileName, audioRequestBody)
                .build();

        Request request = createRequest("/voices/add", requestBody);

        try (Response response = client.newCall(request).execute()) {
            validateResponse(response);
            return extractVoiceId(response.body().string());
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
        RequestBody audioRequestBody = createAudioRequestBody(audioFilePath);

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("model_id", "eleven_english_sts_v2")
                .addFormDataPart("remove_background_noise", "false")
                .addFormDataPart("audio", "source.mp3", audioRequestBody)
                .build();

        Request request = createRequest("/speech-to-speech/" + voiceId, requestBody);

        try (Response response = client.newCall(request).execute()) {
            validateResponse(response);

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
     * 요청 본문 생성에 사용될 오디오 RequestBody 생성.
     *
     * @param audioPath 오디오 파일 경로
     * @return 생성된 RequestBody
     * @throws IOException 파일 처리 중 오류
     */
    private RequestBody createAudioRequestBody(String audioPath) throws IOException {
        if (audioPath.startsWith("http:/") || audioPath.startsWith("https:/")) {
            URL url = new URL(audioPath);
            try (InputStream inputStream = url.openStream()) {
                return RequestBody.create(inputStream.readAllBytes(), MediaType.parse("audio/mpeg"));
            }
        } else {
            throw new IllegalArgumentException("지원하지 않는 파일 경로입니다: " + audioPath);
        }
    }

    /**
     * 파일 경로에서 파일 이름 추출.
     *
     * @param filePath 파일 경로
     * @return 파일 이름
     */
    private String extractFileName(String filePath) {
        return Paths.get(filePath).getFileName().toString();
    }

    /**
     * 요청 생성 메서드.
     *
     * @param endpoint   API 엔드포인트
     * @param requestBody 요청 본문
     * @return 생성된 Request
     */
    private Request createRequest(String endpoint, RequestBody requestBody) {
        return new Request.Builder()
                .url(baseUrl + endpoint)
                .addHeader("xi-api-key", apiKey)
                .post(requestBody)
                .build();
    }

    /**
     * 응답 검증 메서드.
     *
     * @param response HTTP 응답
     * @throws IOException 응답이 성공적이지 않을 경우 예외 발생
     */
    private void validateResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException("API 요청 실패: " + response.body().string());
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