package com.fourformance.tts_vc_web.service.concat;

import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.common.constant.ConcatStatusConst;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.*;
import com.fourformance.tts_vc_web.dto.concat.*;
import com.fourformance.tts_vc_web.repository.*;
import com.fourformance.tts_vc_web.service.common.S3Service;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 오디오 파일 병합 관련 서비스 클래스입니다.
 * 여러 오디오 파일을 병합하고, 무음 처리를 하며, S3에 업로드하는 등의 기능을 제공합니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ConcatService_team_api {

    // 의존성 주입: 다양한 서비스와 리포지토리
    private final S3Service s3Service; // S3 연동 서비스
    private final AudioProcessingService audioProcessingService; // 오디오 처리 서비스
    private final ConcatProjectRepository concatProjectRepository; // 병합 프로젝트 리포지토리
    private final ConcatStatusHistoryRepository concatStatusHistoryRepository; // 병합 상태 이력 리포지토리
    private final ConcatDetailRepository concatDetailRepository; // 병합 디테일 리포지토리
    private final MemberRepository memberRepository; // 멤버 리포지토리
    private final MemberAudioMetaRepository memberAudioMetaRepository; // 멤버 오디오 메타 리포지토리
    private final Environment environment; // 현재 환경 정보를 위한 Environment 객체

    // 로거 설정
    private static final Logger LOGGER = Logger.getLogger(ConcatService_team_api.class.getName());

    // 설정 값 주입
    @Value("${upload.dir}")
    private String uploadDir; // 파일 업로드 디렉토리 경로

    @Value("${ffmpeg.path}")
    private String ffmpegPath; // FFmpeg 실행 파일 경로

    /**
     * 서비스 초기화 메서드입니다.
     * 업로드 디렉토리 생성 및 FFmpeg 경로 검증을 수행합니다.
     */
    @PostConstruct
    public void initialize() {
        // 테스트 환경에서는 초기화를 건너뜁니다.
        if (isTestEnvironment()) {
            LOGGER.info("테스트 환경이므로 initialize 메서드를 건너뜁니다.");
            return;
        }

        // 업로드 디렉토리 생성
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            if (!uploadFolder.mkdirs()) {
                throw new RuntimeException("업로드 디렉토리를 생성할 수 없습니다: " + uploadDir);
            }
        }
        LOGGER.info("업로드 디렉토리가 설정되었습니다: " + uploadDir);

        // FFmpeg 경로 검증
        File ffmpegFile = new File(ffmpegPath);
        if (!ffmpegFile.exists() || !ffmpegFile.canExecute()) {
            throw new RuntimeException("FFmpeg 실행 파일을 찾을 수 없거나 실행 권한이 없습니다: " + ffmpegPath);
        }
        LOGGER.info("FFmpeg 경로가 설정되었습니다: " + ffmpegPath);

        // FFmpeg 인스턴스 초기화
        setupFFmpeg();
    }

    /**
     * 현재 환경이 테스트 환경인지 확인하는 메서드입니다.
     *
     * @return 테스트 환경 여부
     */
    private boolean isTestEnvironment() {
        return Arrays.asList(environment.getActiveProfiles()).contains("test");
    }

    /**
     * FFmpeg 및 FFprobe 인스턴스를 초기화합니다.
     */
    private void setupFFmpeg() {
        try {
            // FFmpeg 및 FFprobe 인스턴스 생성
            FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
            FFprobe ffprobe = new FFprobe(ffmpegPath.replace("ffmpeg", "ffprobe"));
            // 추가적인 FFmpeg 설정 로직이 필요하다면 여기에 추가
        } catch (IOException e) {
            LOGGER.severe("FFmpeg 초기화 오류: " + e.getMessage());
            throw new BusinessException(ErrorCode.FFMPEG_INITIALIZATION_FAILED);
        }
    }

    /**
     * 오디오 파일 병합 프로세스를 수행하는 메서드입니다.
     *
     * @param concatRequestDto 병합 요청 데이터 DTO
     * @param memberId         현재 세션의 멤버 ID
     * @return 병합 결과를 담은 ConcatResponseDto
     */
    public ConcatResponseDto convertAllConcatDetails(ConcatRequestDto concatRequestDto, Long memberId) {
        LOGGER.info("convertAllConcatDetails 호출: " + concatRequestDto);

        // 1. 프로젝트 생성 또는 업데이트
        ConcatProject concatProject = saveOrUpdateProject(concatRequestDto, memberId);

        // 2. 응답 DTO 초기화
        ConcatResponseDto concatResponseDto = initializeResponseDto(concatProject);

        try {
            // 3. 요청된 각 디테일을 처리
            List<ConcatResponseDetailDto> responseDetails = processRequestDetails(concatRequestDto, concatProject);

            // 4. 오디오 파일 병합 및 S3 업로드
            String mergedFileUrl = mergeAudioFilesAndUploadToS3(responseDetails, uploadDir, memberId, concatProject.getId());

            // 응답 DTO에 데이터 설정
            concatResponseDto.setOutputConcatAudios(Collections.singletonList(mergedFileUrl));
            concatResponseDto.setConcatResponseDetails(responseDetails);

            // 성공 시 병합 상태 이력 저장
            saveConcatStatusHistory(concatProject, ConcatStatusConst.SUCCESS);

            return concatResponseDto;

        } catch (Exception e) {
            // 예외 발생 시 로그 기록 및 상태 이력 저장
            LOGGER.severe("오류 발생: " + e.getMessage());
            saveConcatStatusHistory(concatProject, ConcatStatusConst.FAILURE);
            throw e; // 예외를 다시 던짐
        }
    }

    /**
     * 요청된 디테일들을 처리하여 응답 디테일 리스트를 반환합니다.
     *
     * @param concatRequestDto 요청 DTO
     * @param concatProject    현재 병합 프로젝트
     * @return 응답 디테일 리스트
     */
    private List<ConcatResponseDetailDto> processRequestDetails(ConcatRequestDto concatRequestDto, ConcatProject concatProject) {
        List<ConcatResponseDetailDto> responseDetails = new ArrayList<>();

        for (ConcatRequestDetailDto detailDto : concatRequestDto.getConcatRequestDetails()) {
            try {
                LOGGER.info("ConcatDetail 처리 시작: " + detailDto);
                ConcatDetail concatDetail;
                MemberAudioMeta memberAudioMeta = null;

                // 파일이 업로드된 경우 처리
                if (detailDto.getSourceAudio() != null && !detailDto.getSourceAudio().isEmpty()) {
                    // 새로운 파일을 S3에 업로드하고 MemberAudioMeta 생성
                    memberAudioMeta = uploadConcatDetailSourceAudio(detailDto, concatProject);
                } else if (detailDto.getMemberAudioId() != null) {
                    // 기존의 MemberAudioMeta를 가져옴
                    memberAudioMeta = memberAudioMetaRepository.findById(detailDto.getMemberAudioId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_AUDIO_META_NOT_FOUND));
                } else if (detailDto.getId() != null) {
                    // 기존 ConcatDetail에서 MemberAudioMeta를 가져옴
                    ConcatDetail existingConcatDetail = concatDetailRepository.findById(detailDto.getId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT_DETAIL));
                    memberAudioMeta = existingConcatDetail.getMemberAudioMeta();
                }

                // 디테일 저장 또는 업데이트
                concatDetail = saveOrUpdateDetail(detailDto, concatProject, memberAudioMeta);

                // 응답 디테일 생성 및 추가
                responseDetails.add(createResponseDetailDto(concatDetail));

                LOGGER.info("ConcatDetail 처리 완료 - ID: " + concatDetail.getId());

            } catch (Exception e) {
                // 디테일 처리 중 예외 발생 시 로그 기록 및 예외 던짐
                LOGGER.severe("ConcatDetail 처리 중 오류 발생: " + detailDto + ", 메시지: " + e.getMessage());
                throw new BusinessException(ErrorCode.TTS_DETAIL_PROCESSING_FAILED);
            }
        }

        return responseDetails;
    }

    /**
     * 오디오 파일을 병합하고 S3에 업로드하는 메서드입니다.
     *
     * @param audioDetails 병합할 오디오 디테일 리스트
     * @param uploadDir    업로드 디렉토리 경로
     * @param userId       사용자 ID
     * @param projectId    프로젝트 ID
     * @return 병합된 오디오 파일의 S3 URL
     */
    public String mergeAudioFilesAndUploadToS3(List<ConcatResponseDetailDto> audioDetails, String uploadDir, Long userId, Long projectId) {
        List<String> savedFilePaths = new ArrayList<>();   // 다운로드된 오디오 파일 경로 리스트
        List<String> silenceFilePaths = new ArrayList<>(); // 생성된 무음 파일 경로 리스트
        String mergedFilePath = null;                      // 병합된 오디오 파일 경로

        try {
            // 1. 체크된 파일들만 필터링
            List<ConcatResponseDetailDto> filteredDetails = audioDetails.stream()
                    .filter(ConcatResponseDetailDto::isChecked)
                    .collect(Collectors.toList());

            if (filteredDetails.isEmpty()) {
                LOGGER.severe("병합할 파일이 없습니다.");

                // 실패 시 병합 상태 이력 저장
                saveConcatStatusHistory(concatProjectRepository.findById(projectId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT)), ConcatStatusConst.FAILURE);

                throw new BusinessException(ErrorCode.NO_FILES_TO_MERGE);
            }

            // 2. S3에서 파일 다운로드 및 무음 파일 생성
            for (ConcatResponseDetailDto detail : filteredDetails) {
                if (detail.getAudioUrl() != null && !detail.getAudioUrl().isEmpty()) {
                    // S3에서 오디오 파일 다운로드
                    String savedFilePath = s3Service.downloadFileFromS3(detail.getAudioUrl(), uploadDir);
                    savedFilePaths.add(savedFilePath);

                    // 무음 파일 생성
                    String silenceFilePath = audioProcessingService.createSilenceFile(detail.getEndSilence().longValue(), uploadDir);
                    if (silenceFilePath != null) silenceFilePaths.add(silenceFilePath);
                } else {
                    LOGGER.warning("Audio URL이 없습니다. Detail ID: " + detail.getId());
                    throw new BusinessException(ErrorCode.AUDIO_URL_NOT_FOUND);
                }
            }

            // 3. 오디오 파일 병합
            mergedFilePath = audioProcessingService.mergeAudioFilesWithSilence(savedFilePaths, silenceFilePaths, uploadDir);

            // 4. 병합된 파일을 S3에 업로드하고 URL 반환
            return s3Service.uploadConcatSaveFile(audioProcessingService.convertToMultipartFile(mergedFilePath), userId, projectId);

        } catch (IOException e) {
            LOGGER.severe("파일 처리 중 IOException 발생: " + e.getMessage());
            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
        } catch (BusinessException e) {
            LOGGER.severe("비즈니스 예외 발생: " + e.getErrorCode().getMessage());
            throw e;
        } finally {
            // 5. 임시 파일 삭제
            cleanupTemporaryFiles(savedFilePaths, silenceFilePaths, mergedFilePath);
        }
    }

    /**
     * 프로젝트를 생성하거나 업데이트합니다.
     *
     * @param concatRequestDto 요청 DTO
     * @param memberId         멤버 ID
     * @return 저장된 ConcatProject 엔티티
     */
    private ConcatProject saveOrUpdateProject(ConcatRequestDto concatRequestDto, Long memberId) {
        return Optional.ofNullable(concatRequestDto.getProjectId())
                .map(projectId -> {
                    // 프로젝트 업데이트
                    updateProject(concatRequestDto, memberId);
                    return concatProjectRepository.findById(projectId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT));
                })
                .orElseGet(() -> {
                    // 새로운 프로젝트 생성
                    return createNewProject(concatRequestDto, memberId);
                });
    }

    /**
     * 새로운 프로젝트를 생성합니다.
     *
     * @param dto      요청 DTO
     * @param memberId 멤버 ID
     * @return 생성된 ConcatProject 엔티티
     */
    private ConcatProject createNewProject(ConcatRequestDto dto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 프로젝트 생성
        ConcatProject concatProject = ConcatProject.createConcatProject(member, dto.getProjectName());

        // 프로젝트 전역 설정 값 업데이트
        if (dto.getGlobalFrontSilenceLength() != 0.0F || dto.getGlobalTotalSilenceLength() != 0.0F || dto.getProjectName() != null) {
            concatProject.updateConcatProject(dto.getProjectName(), dto.getGlobalFrontSilenceLength(), dto.getGlobalTotalSilenceLength());
        }

        return concatProjectRepository.save(concatProject);
    }

    /**
     * 기존 프로젝트를 업데이트합니다.
     *
     * @param dto      요청 DTO
     * @param memberId 멤버 ID
     */
    private void updateProject(ConcatRequestDto dto, Long memberId) {
        ConcatProject project = concatProjectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT));

        // 멤버 ID가 일치하는지 확인
        if (!project.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.MEMBER_PROJECT_NOT_MATCH);
        }

        // 프로젝트 정보 업데이트
        project.updateConcatProject(dto.getProjectName(), dto.getGlobalFrontSilenceLength(), dto.getGlobalTotalSilenceLength());
    }

    /**
     * 디테일을 저장하거나 업데이트합니다.
     *
     * @param detailDto        디테일 요청 DTO
     * @param concatProject    현재 병합 프로젝트
     * @param memberAudioMeta  멤버 오디오 메타 정보
     * @return 저장된 ConcatDetail 엔티티
     */
    private ConcatDetail saveOrUpdateDetail(ConcatRequestDetailDto detailDto, ConcatProject concatProject, MemberAudioMeta memberAudioMeta) {
        return Optional.ofNullable(detailDto.getId())
                .map(id -> {
                    // 디테일 업데이트
                    updateConcatDetail(detailDto, concatProject, memberAudioMeta);
                    return concatDetailRepository.findById(id)
                            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT_DETAIL));
                })
                .orElseGet(() -> {
                    // 새로운 ConcatDetail 생성
                    return concatDetailRepository.save(
                            ConcatDetail.createConcatDetail(
                                    concatProject,
                                    detailDto.getAudioSeq(),
                                    detailDto.isChecked(),
                                    detailDto.getUnitScript(),
                                    detailDto.getEndSilence(),
                                    memberAudioMeta
                            )
                    );
                });
    }

    /**
     * 기존 디테일을 업데이트합니다.
     *
     * @param detailDto        디테일 요청 DTO
     * @param concatProject    현재 병합 프로젝트
     * @param memberAudioMeta  멤버 오디오 메타 정보
     */
    private void updateConcatDetail(ConcatRequestDetailDto detailDto, ConcatProject concatProject, MemberAudioMeta memberAudioMeta) {
        ConcatDetail concatDetail = concatDetailRepository.findById(detailDto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT_DETAIL));

        // 프로젝트 ID가 일치하는지 확인
        if (!concatDetail.getConcatProject().getId().equals(concatProject.getId())) {
            throw new BusinessException(ErrorCode.NOT_EXISTS_PROJECT_DETAIL);
        }

        // 새로운 파일이 업로드된 경우에만 MemberAudioMeta 업데이트
        if (memberAudioMeta != null) {
            concatDetail.injectMemberAudioMeta(memberAudioMeta);
        }

        // 디테일 정보 업데이트
        concatDetail.updateDetails(
                detailDto.getAudioSeq(),
                detailDto.isChecked(),
                detailDto.getUnitScript(),
                detailDto.getEndSilence()
        );
    }

    /**
     * 디테일의 소스 오디오를 S3에 업로드하고 MemberAudioMeta를 반환합니다.
     *
     * @param detailDto     디테일 요청 DTO
     * @param concatProject 현재 병합 프로젝트
     * @return 생성된 MemberAudioMeta 엔티티
     */
    private MemberAudioMeta uploadConcatDetailSourceAudio(ConcatRequestDetailDto detailDto, ConcatProject concatProject) {
        // 파일을 S3에 업로드하고 MemberAudioMeta 생성
        List<MemberAudioMeta> memberAudioMetas = s3Service.uploadAndSaveMemberFile2(
                Collections.singletonList(detailDto.getSourceAudio()),
                concatProject.getMember().getId(),
                concatProject.getId(),
                AudioType.CONCAT
        );

        // 업로드된 첫 번째 파일의 MemberAudioMeta 반환
        return memberAudioMetas.get(0);
    }

    /**
     * 응답 DTO를 초기화합니다.
     *
     * @param concatProject 현재 병합 프로젝트
     * @return 초기화된 ConcatResponseDto
     */
    private ConcatResponseDto initializeResponseDto(ConcatProject concatProject) {
        return ConcatResponseDto.builder()
                .projectId(concatProject.getId())
                .projectName(concatProject.getProjectName())
                .globalFrontSilenceLength(concatProject.getGlobalFrontSilenceLength())
                .globalTotalSilenceLength(concatProject.getGlobalTotalSilenceLength())
                .build();
    }

    /**
     * 응답 디테일 DTO를 생성합니다.
     *
     * @param concatDetail ConcatDetail 엔티티
     * @return 생성된 ConcatResponseDetailDto
     */
    private ConcatResponseDetailDto createResponseDetailDto(ConcatDetail concatDetail) {
        return ConcatResponseDetailDto.builder()
                .id(concatDetail.getId())
                .audioSeq(concatDetail.getAudioSeq())
                .isChecked(concatDetail.isChecked())
                .unitScript(concatDetail.getUnitScript())
                .endSilence(concatDetail.getEndSilence())
                .audioUrl(concatDetail.getMemberAudioMeta() != null ? concatDetail.getMemberAudioMeta().getAudioUrl() : null)
                .build();
    }

    /**
     * 임시 파일들을 정리합니다.
     *
     * @param savedFilePaths   다운로드된 오디오 파일 경로 리스트
     * @param silenceFilePaths 생성된 무음 파일 경로 리스트
     * @param mergedFilePath   병합된 오디오 파일 경로
     */
    private void cleanupTemporaryFiles(List<String> savedFilePaths, List<String> silenceFilePaths, String mergedFilePath) {
        audioProcessingService.deleteFiles(savedFilePaths);
        audioProcessingService.deleteFiles(silenceFilePaths);
        if (mergedFilePath != null) {
            audioProcessingService.deleteFiles(Collections.singletonList(mergedFilePath));
        }
    }

    /**
     * 병합 상태 이력을 저장합니다.
     *
     * @param concatProject 현재 병합 프로젝트
     * @param status        저장할 상태 (성공 또는 실패)
     */
    private void saveConcatStatusHistory(ConcatProject concatProject, ConcatStatusConst status) {
        ConcatStatusHistory concatStatusHistory = ConcatStatusHistory.createConcatStatusHistory(concatProject, status);
        concatStatusHistoryRepository.save(concatStatusHistory);
    }
}
