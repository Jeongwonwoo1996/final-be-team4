package com.fourformance.tts_vc_web.service.tts;

import com.fourformance.tts_vc_web.common.constant.APIStatusConst;
import com.fourformance.tts_vc_web.common.constant.APIUnitStatusConst;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.common.util.CommonFileUtils;
import com.fourformance.tts_vc_web.common.util.GoogleTTSClient;
import com.fourformance.tts_vc_web.domain.entity.APIStatus;
import com.fourformance.tts_vc_web.domain.entity.TTSDetail;
import com.fourformance.tts_vc_web.domain.entity.TTSProject;
import com.fourformance.tts_vc_web.dto.tts.*;
import com.fourformance.tts_vc_web.repository.APIStatusRepository;
import com.fourformance.tts_vc_web.repository.TTSDetailRepository;
import com.fourformance.tts_vc_web.repository.TTSProjectRepository;
import com.fourformance.tts_vc_web.repository.VoiceStyleRepository;
import com.fourformance.tts_vc_web.service.common.S3Service;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

@Service
@Transactional
@RequiredArgsConstructor
public class TTSService_team_api {

    // 의존성 주입: Repository와 기타 서비스들
    private final TTSProjectRepository ttsProjectRepository; // 프로젝트 데이터에 접근하기 위한 Repository
    private final TTSDetailRepository ttsDetailRepository; // TTS 디테일 데이터에 접근하기 위한 Repository
    private final APIStatusRepository apiStatusRepository; // API 상태를 저장하고 조회하기 위한 Repository
    private final VoiceStyleRepository voiceStyleRepository;
    private final TTSService_team_multi ttsServiceTeamMulti; // 통합 서비스 호출을 위한 클래스
    private final S3Service s3Service; // S3 파일 업로드를 처리하는 서비스
    private final GoogleTTSClient googleTTSClient; // GoogleTTSClient 주입

    private static final Logger LOGGER = Logger.getLogger(TTSService_team_api.class.getName()); // 로그 기록을 위한 Logger

    /**
     * 모든 TTS 디테일 처리: 데이터를 생성 또는 업데이트하고 오디오 파일을 생성.
     *
     * @param ttsRequestDto 프로젝트와 디테일 데이터를 포함한 DTO
     * @return 생성된 오디오 파일 경로 리스트
     */
    public TTSResponseDto convertAllTtsDetails(TTSRequestDto ttsRequestDto, Long memberId) {
        LOGGER.info("convertAllTtsDetails 호출: " + ttsRequestDto);

        // 1. 프로젝트 저장 또는 업데이트
        TTSProject ttsProject = saveOrUpdateProject(ttsRequestDto, memberId);

        // 2. 응답 DTO 생성 및 초기화
        TTSResponseDto.TTSResponseDtoBuilder responseBuilder = TTSResponseDto.builder()
                .projectId(ttsProject.getId())
                .projectName(ttsProject.getProjectName())
                .globalVoiceStyleId(ttsProject.getVoiceStyle().getId())
                .fullScript(ttsProject.getFullScript())
                .globalSpeed(ttsProject.getGlobalSpeed())
                .globalPitch(ttsProject.getGlobalPitch())
                .globalVolume(ttsProject.getGlobalVolume())
                .apiStatus(APIStatusConst.IN_PROGRESS)
                .ttsDetails(new ArrayList<>());

        // 프로젝트의 TTS 디테일 데이터 처리
        List<TTSResponseDetailDto> responseDetails = new ArrayList<>();

        int successCount = 0;
        int failureCount = 0;

        for (TTSRequestDetailDto ttsRequestDetailDto : ttsRequestDto.getTtsDetails()) {
            // 디테일 데이터 저장 또는 업데이트
            TTSDetail ttsDetail = saveOrUpdateDetail(ttsRequestDetailDto, ttsProject);

            try {
                LOGGER.info("TTSDetail 처리 시작: " + ttsRequestDetailDto);
                // 디테일 처리 및 오디오 파일 경로 저장

                Map<String, String> fileUrlMap = processTtsDetail(ttsRequestDetailDto, ttsProject);
                String fileUrl = fileUrlMap.get("fileUrl");

                // TTSResponseDetailDto 생성 및 추가
                TTSResponseDetailDto responseDetail = TTSResponseDetailDto.builder()
                        .id(ttsDetail.getId())
                        .projectId(ttsProject.getId())
                        .unitScript(ttsDetail.getUnitScript())
                        .unitSpeed(ttsDetail.getUnitSpeed())
                        .unitPitch(ttsDetail.getUnitPitch())
                        .unitVolume(ttsDetail.getUnitVolume())
                        .isDeleted(ttsDetail.getIsDeleted())
                        .unitSequence(ttsDetail.getUnitSequence())
                        .UnitVoiceStyleId(ttsDetail.getVoiceStyle().getId())
                        .fileUrl(fileUrl) // 처리된 URL 삽입
                        .apiUnitStatus(APIUnitStatusConst.SUCCESS)
                        .build();

                responseDetails.add(responseDetail);
                successCount++;
                LOGGER.info("TTSDetail 처리 완료: " + ttsRequestDetailDto);


            } catch (BusinessException e) {
                LOGGER.severe("TTSDetail 처리 중 오류 발생: " + ttsRequestDetailDto + ", 메시지: " + e.getMessage());
                // 실패한 디테일에 대한 실패 응답 추가
                TTSResponseDetailDto failedDetail = TTSResponseDetailDto.builder()
                        .id(ttsRequestDetailDto.getId())
                        .projectId(ttsProject.getId())
                        .unitScript(ttsRequestDetailDto.getUnitScript())
//                        .errorMessage(e.getMessage())
                        .apiUnitStatus(APIUnitStatusConst.FAILURE)
                        .build();
                responseDetails.add(failedDetail);
                failureCount++;
                // 계속 진행
            }
        }

        // 전체 API 상태 업데이트
        if (failureCount == 0) {
            ttsProject.updateAPIStatus(APIStatusConst.SUCCESS);
            responseBuilder.apiStatus(APIStatusConst.SUCCESS);
        } else if (successCount == 0) {
            ttsProject.updateAPIStatus(APIStatusConst.FAILURE);
            responseBuilder.apiStatus(APIStatusConst.FAILURE);
        } else {
            ttsProject.updateAPIStatus(APIStatusConst.PARTIAL_FAILURE);
            responseBuilder.apiStatus(APIStatusConst.PARTIAL_FAILURE);
        }
        ttsProjectRepository.save(ttsProject);

        // 응답 DTO에 디테일 정보 추가
        responseBuilder.ttsDetails(responseDetails);

        LOGGER.info("convertAllTtsDetails 완료: 생성된 ResponseDto = " + responseBuilder.build());
        return responseBuilder.build();
    }

    /**
     * 프로젝트 저장 또는 업데이트
     *
     * @param ttsRequestDto 프로젝트 데이터를 포함한 DTO
     * @return 저장된 프로젝트 엔티티
     */
    private TTSProject saveOrUpdateProject(TTSRequestDto ttsRequestDto, Long memberId) {
        // 프로젝트 ID가 존재하는 경우 업데이트, 없으면 새로 생성
        Long projectId = Optional.ofNullable(ttsRequestDto.getProjectId())
                .map(id -> {
                    // 기존 프로젝트 업데이트
                    ttsServiceTeamMulti.updateProjectCustom(ttsRequestDto);
                    return id;
                })
                .orElseGet(() -> {
                    // 새로운 프로젝트 생성
                    return ttsServiceTeamMulti.createNewProjectCustom(ttsRequestDto, memberId);
                });

        // ID를 통해 프로젝트를 조회하고 반환
        return ttsProjectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TTS_PROJECT_NOT_FOUND));
    }

    /**
     * TTS 디테일 저장 또는 업데이트
     *
     * @param ttsRequestDetailDto TTS 디테일 데이터를 포함한 DTO
     * @param ttsProject 연결된 프로젝트
     * @return 저장된 TTS 디테일 엔티티
     */
    private TTSDetail saveOrUpdateDetail(TTSRequestDetailDto ttsRequestDetailDto, TTSProject ttsProject) {
        // ID가 없으면 새로 생성, 있으면 업데이트
        if (ttsRequestDetailDto.getId() == null) {
            ttsRequestDetailDto.setId(ttsServiceTeamMulti.createTTSDetailCustom(ttsRequestDetailDto, ttsProject));
        } else {
            ttsRequestDetailDto.setId(ttsServiceTeamMulti.processTTSDetailCustom(ttsRequestDetailDto, ttsProject));
        }

        // 저장된 디테일 데이터를 조회하고 반환
        return ttsDetailRepository.findById(ttsRequestDetailDto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TTS_DETAIL_NOT_FOUND));
    }

    /**
     * TTS 디테일 처리: Google TTS API를 통해 텍스트를 오디오로 변환하고 저장
     *
     * @param ttsRequestDetailDto TTS 디테일 데이터를 포함한 DTO
     * @param ttsProject 연결된 프로젝트
     * @return 변환된 오디오 파일 경로를 포함한 Map
     */
    private Map<String, String> processTtsDetail(TTSRequestDetailDto ttsRequestDetailDto, TTSProject ttsProject) {
        // Google TTS API 호출로 오디오 데이터 생성
        ByteString audioContent = callTTSApi(ttsRequestDetailDto, ttsProject);
        // 오디오 파일을 저장하고 URL 반환
        String fileUrl = saveAudioFile(audioContent, ttsRequestDetailDto.getUnitSequence(),
                ttsProject.getMember().getId(), ttsProject.getId(), ttsRequestDetailDto.getId());
        return Map.of("fileUrl", fileUrl);
    }

    /**
     * Google TTS API 호출: 텍스트를 오디오로 변환
     *
     * @param ttsRequestDetailDto TTS 디테일 데이터를 포함한 DTO
     * @return 변환된 오디오 데이터(ByteString)
     */
    private ByteString callTTSApi(TTSRequestDetailDto ttsRequestDetailDto, TTSProject ttsProject) {
        LOGGER.info("callTTSApi 호출: " + ttsRequestDetailDto);

        // TTS 디테일과 음성 스타일 데이터 조회
        TTSDetail ttsDetail = ttsDetailRepository.findById(ttsProject.getMember().getId()).orElseThrow();
        String languageCode = voiceStyleRepository.findById(ttsRequestDetailDto.getUnitVoiceStyleId()).get().getLanguageCode();
        String gender = voiceStyleRepository.findById(ttsRequestDetailDto.getUnitVoiceStyleId()).get().getGender();
        String script = ttsRequestDetailDto.getUnitScript();


        System.out.println("gender = " + gender);
        System.out.println("languageCode = " + languageCode);
        System.out.println("script = " + script);

        // 텍스트와 언어 코드 검증
        checkTextLanguage(script, languageCode);

        // 요청 페이로드 생성
        String requestPayload = String.format(
                "{ \"text\": \"%s\", \"language\": \"%s\", \"gender\": \"%s\", \"speed\": %.2f, \"volume\": %.2f, \"pitch\": %.2f }",
                ttsRequestDetailDto.getUnitScript(),
                languageCode,
                gender,
                ttsRequestDetailDto.getUnitSpeed(),
                ttsRequestDetailDto.getUnitVolume(),
                ttsRequestDetailDto.getUnitPitch()
        );


        // APIStatus 엔티티 생성 및 저장
        APIStatus apiStatus = APIStatus.createAPIStatus(null, ttsDetail, requestPayload);
        apiStatusRepository.save(apiStatus);

        try {
            // 주입받은 GoogleTTSClient 통해 TextToSpeechClient 사용
            TextToSpeechClient textToSpeechClient = googleTTSClient.getTextToSpeechClient();

            // Google TTS API 요청 생성
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(Optional.ofNullable(ttsRequestDetailDto.getUnitScript())
                            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_UNIT_SCRIPT)))
                    .build();

            // 음성 및 오디오 설정 생성
            SsmlVoiceGender ssmlGender = getSsmlVoiceGender(ttsRequestDetailDto);
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(languageCode)
                    .setSsmlGender(ssmlGender)
                    .build();

            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.LINEAR16)
                    .setSpeakingRate(Optional.ofNullable(ttsRequestDetailDto.getUnitSpeed()).orElse(1.0F))
                    .setVolumeGainDb(Optional.ofNullable(ttsRequestDetailDto.getUnitVolume()).orElse(0.0F))
                    .setPitch(Optional.ofNullable(ttsRequestDetailDto.getUnitPitch()).orElse(0.0F))
                    .build();

            // Google TTS API 호출
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // 응답 데이터를 JSON으로 변환
            String responsePayload = String.format(
                    "{ \"audioSize\": \"%d\", \"contentType\": \"audio/linear16\", \"request\": %s }",
                    response.getAudioContent().size(),
                    requestPayload
            );

            // 응답 검증 및 처리
            if (response.getAudioContent().isEmpty()) {
                apiStatus.updateResponseInfo(requestPayload, 500, APIUnitStatusConst.FAILURE);
                apiStatusRepository.save(apiStatus); // 상태 저장

                ttsProject.updateAPIStatus(APIStatusConst.FAILURE);
                ttsProjectRepository.save(ttsProject);

                throw new BusinessException(ErrorCode.TTS_CONVERSION_FAILED_EMPTY_CONTENT);
            }

            // 성공적인 처리
            apiStatus.updateResponseInfo(responsePayload, 200, APIUnitStatusConst.SUCCESS);
            apiStatusRepository.save(apiStatus);

            LOGGER.info("Google TTS API 호출 성공");
            return response.getAudioContent();
        } catch (Exception e) {

            apiStatus.updateResponseInfo(requestPayload, 500, APIUnitStatusConst.FAILURE);
            apiStatusRepository.save(apiStatus); // 상태 저장

            ttsProject.updateAPIStatus(APIStatusConst.FAILURE);
            ttsProjectRepository.save(ttsProject);

            LOGGER.severe("Google TTS API 호출 중 오류: " + e.getMessage());
            throw new BusinessException(ErrorCode.TTS_CONVERSION_FAILED);
        }
    }

    /**
     * VoiceStyle의 Gender를 SsmlVoiceGender로 변환
     *
     * @param ttsRequestDetailDto TTS 디테일 데이터를 포함한 DTO
     * @return 변환된 SsmlVoiceGender
     */
    private SsmlVoiceGender getSsmlVoiceGender(TTSRequestDetailDto ttsRequestDetailDto) {
        // 음성 스타일의 Gender 데이터를 가져와 변환
        String gender = voiceStyleRepository.findById(ttsRequestDetailDto.getUnitVoiceStyleId()).get().getGender();
        return switch (gender.toLowerCase()) {
            case "male" -> SsmlVoiceGender.MALE;
            case "female" -> SsmlVoiceGender.FEMALE;
            default -> SsmlVoiceGender.NEUTRAL;
        };
    }

    /**
     * 변환된 오디오 파일을 저장
     *
     * @param audioContent 변환된 오디오 데이터
     * @param sequence 유닛 시퀀스
     * @param userId 사용자 ID
     * @param projectId 프로젝트 ID
     * @param detailId 디테일 ID
     * @return 저장된 오디오 파일 URL
     */
    private String saveAudioFile(ByteString audioContent, int sequence, Long userId, Long projectId, Long detailId) {
        String fileName = "tts_audio_" + sequence + ".wav";
        LOGGER.info("saveAudioFile 호출: fileName = " + fileName);

        try {
            // 임시 파일 생성 및 저장
            File tempFile = File.createTempFile("tts_audio_", ".wav");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(audioContent.toByteArray());
            }

            // S3에 업로드
            MultipartFile multipartFile = CommonFileUtils.convertFileToMultipartFile(tempFile, fileName);
            String s3FileUrl = s3Service.uploadUnitSaveFile(multipartFile, userId, projectId, detailId);

            // 임시 파일 삭제
            if (!tempFile.delete()) {
                LOGGER.warning("임시 파일 삭제 실패: " + tempFile.getAbsolutePath());
            }

            LOGGER.info("S3 업로드 성공: " + s3FileUrl);
            return s3FileUrl;
        } catch (IOException e) {
            LOGGER.severe("saveAudioFile 실패: " + e.getMessage());
            throw new BusinessException(ErrorCode.AUDIO_FILE_SAVE_ERROR);
        }
    }

    /**
     * 텍스트와 언어 코드의 일치 여부를 검증
     * @param text 텍스트 데이터
     * @param languageCode 언어 코드
     */
    private void checkTextLanguage(String text, String languageCode) {
        // 언어 코드 형식 검증 (xx-XX 형식)
        if (!languageCode.matches("^[a-z]{2}-[A-Z]{2}$")) {
            throw new BusinessException(ErrorCode.INVALID_LANGUAGE_CODE_FORMAT);
        }

        // 지원되는 언어 코드 리스트
        Set<String> supportedLanguageCodes = Set.of(
                "af-ZA", "ar-XA", "eu-ES", "bn-IN", "bg-BG", "ca-ES", "yue-HK", "cs-CZ",
                "da-DK", "nl-BE", "nl-NL", "en-AU", "en-IN", "en-GB", "en-US", "fil-PH",
                "fi-FI", "fr-CA", "fr-FR", "gl-ES", "de-DE", "el-GR", "gu-IN", "he-IL",
                "hi-IN", "hu-HU", "is-IS", "id-ID", "it-IT", "ja-JP", "kn-IN", "ko-KR",
                "lv-LV", "lt-LT", "ms-MY", "ml-IN", "cmn-CN", "cmn-TW", "mr-IN", "nb-NO",
                "pl-PL", "pt-BR", "pt-PT", "pa-IN", "ro-RO", "ru-RU", "sr-RS", "sk-SK",
                "es-ES", "es-US", "sv-SE", "ta-IN", "te-IN", "th-TH", "tr-TR", "uk-UA",
                "vi-VN"
        );

        // 언어 코드 유효성 검증
        if (!supportedLanguageCodes.contains(languageCode)) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_LANGUAGE_CODE);
        }

        // 텍스트와 언어 코드에 따른 일치 여부 검증
        boolean isKorean = text.matches(".*[가-힣].*");
        boolean isChinese = text.matches(".*[\\u4E00-\\u9FFF].*");
        boolean isJapanese = text.matches(".*[\\u3040-\\u30FF\\u31F0-\\u31FF].*");
        boolean isEnglish = text.matches(".*[A-Za-z].*");

        switch (languageCode) {
            case "ko-KR": // 한국어
                if (!isKorean) throw new BusinessException(ErrorCode.INVALID_TEXT_FOR_KO_KR);
                break;
            case "cmn-CN": // 중국어 간체
            case "cmn-TW": // 중국어 번체
            case "yue-HK": // 광둥어
                if (!isChinese) throw new BusinessException(ErrorCode.INVALID_TEXT_FOR_CHINESE);
                break;
            case "ja-JP": // 일본어
                if (!isJapanese) throw new BusinessException(ErrorCode.INVALID_TEXT_FOR_JA_JP);
                break;
            case "en-US": // 영어 (미국)
            case "en-GB": // 영어 (영국)
            case "en-AU": // 영어 (호주)
            case "en-IN": // 영어 (인도)
                if (!isEnglish) throw new BusinessException(ErrorCode.INVALID_TEXT_FOR_EN);
                break;
            case "fr-FR": // 프랑스어
            case "fr-CA": // 프랑스어 (캐나다)
            case "es-ES": // 스페인어
            case "es-US": // 스페인어 (미국)
            case "pt-BR": // 포르투갈어 (브라질)
            case "pt-PT": // 포르투갈어 (포르투갈)
            case "de-DE": // 독일어
            case "it-IT": // 이탈리아어
            case "nl-BE": // 네덜란드어 (벨기에)
            case "nl-NL": // 네덜란드어 (네덜란드)
                if (!text.matches(".*[A-Za-zÀ-ÿ].*")) { // 라틴 문자 포함 확인
                    throw new BusinessException(ErrorCode.INVALID_TEXT_FOR_LATIN_BASED);
                }
                break;
            case "ru-RU": // 러시아어
            case "uk-UA": // 우크라이나어
            case "bg-BG": // 불가리아어
            case "sr-RS": // 세르비아어
                if (!text.matches(".*[А-яЁё].*")) { // 키릴 문자 포함 확인
                    throw new BusinessException(ErrorCode.INVALID_TEXT_FOR_CYRILLIC);
                }
                break;
            case "th-TH": // 태국어
                if (!text.matches(".*[ก-๛].*")) {
                    throw new BusinessException(ErrorCode.INVALID_TEXT_FOR_THAI);
                }
                break;
            case "he-IL": // 히브리어
                if (!text.matches(".*[א-ת].*")) {
                    throw new BusinessException(ErrorCode.INVALID_TEXT_FOR_HEBREW);
                }
                break;
            case "ar-XA": // 아랍어
                if (!text.matches(".*[؀-ۿ].*")) {
                    throw new BusinessException(ErrorCode.INVALID_TEXT_FOR_ARABIC);
                }
                break;
            case "fil-PH": // 필리핀어
            case "id-ID": // 인도네시아어
            case "ms-MY": // 말레이어
                if (!isEnglish) { // 필리핀어, 인도네시아어, 말레이어는 라틴 문자 기반
                    throw new BusinessException(ErrorCode.INVALID_TEXT_FOR_LATIN_BASED);
                }
                break;
            default:
                // 기타 언어 처리 (추가 검증 로직이 없으면 통과)
                LOGGER.info("추가 검증 없이 언어 코드 통과: " + languageCode);
        }
    }

}
