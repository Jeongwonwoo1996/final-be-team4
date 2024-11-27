package com.fourformance.tts_vc_web.service.tts;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.*;
import com.fourformance.tts_vc_web.domain.entity.VoiceStyle;
import com.fourformance.tts_vc_web.dto.common.GeneratedAudioDto;
import com.fourformance.tts_vc_web.dto.tts.*;
import com.fourformance.tts_vc_web.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class TTSService_team_multi {

    private final TTSProjectRepository ttsProjectRepository;
    private final TTSDetailRepository ttsDetailRepository;
    private final VoiceStyleRepository voiceStyleRepository;
    private final OutputAudioMetaRepository outputAudioMetaRepository;
    private final MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    // TTS 프로젝트 값 조회하기
    @Transactional(readOnly = true)
    public TTSProjectDto getTTSProjectDto(Long projectId) {
        // 프로젝트 조회
        TTSProject ttsProject = ttsProjectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT));

        // TTSProjectDTO로 변환
        return TTSProjectDto.createTTSProjectDto(ttsProject);
    }

    // TTS 프로젝트 상세 값 조회하기
    @Transactional(readOnly = true)
    public List<TTSDetailDto> getTTSDetailsDto(Long projectId) {
        List<TTSDetail> ttsDetails = ttsDetailRepository.findByTtsProject_Id(projectId);

        // 생성된 오디오 추가하기
        
        // isDeleted가 false인 경우에만 TTSDetailDTO 목록으로 변환
        return ttsDetails.stream()
                .filter(detail -> !detail.getIsDeleted()) // 삭제되지 않은 항목만 필터링
                .map(detail -> {
                    // OutputAudioMeta를 ttsDetailId로 조회
                    List<OutputAudioMeta> outputAudioMetas = outputAudioMetaRepository.findByTtsDetailIdAndIsDeletedFalse(detail.getId());

                    // OutputAudioMeta를 GeneratedAudioDto로 변환
                    List<GeneratedAudioDto> generatedAudioDtos = outputAudioMetas.stream()
                            .map(meta -> new GeneratedAudioDto(meta.getId(), meta.getAudioUrl()))
                            .toList();

                    // TTSDetailDto 생성 및 GeneratedAudioDto 추가
                    TTSDetailDto ttsDetailDto = TTSDetailDto.createTTSDetailDto(detail);
                    ttsDetailDto.setGenAudios(generatedAudioDtos);
                    return ttsDetailDto;
                })
                .collect(Collectors.toList());
    }


    // 프로젝트 생성
    @Transactional
    public Long createNewProject(TTSSaveDto dto, Long memberId) {

        validateSaveDto(dto);

        VoiceStyle voiceStyle = null;

        // voiceStyleId가 null이 아닌 경우에만 조회 ( voiceStyleId에 null을 허용한다는 의미입니다. 보이스 스타일을 지정하지 않고 저장할 수 있으니까 )
        if (dto.getGlobalVoiceStyleId() != null) {
            voiceStyle = em.merge(voiceStyleRepository.findById(dto.getGlobalVoiceStyleId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_VOICESTYLE)));
        }

        // 받아온 멤버 id (세션에서) 로 멤버 찾기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // TTSDetailDto 리스트에 대한 unitSequence 검증
        if (dto.getTtsDetails() != null) {
            validateUnitSequence(dto.getTtsDetails());
        }

        // TTSProject 생성
        TTSProject ttsProject = TTSProject.createTTSProject(
                member,
                dto.getProjectName(),
                voiceStyle,
                dto.getFullScript(),
                dto.getGlobalSpeed(),
                dto.getGlobalPitch(),
                dto.getGlobalVolume()
        );


        //tts 프로젝트 db에 저장
        ttsProject = ttsProjectRepository.save(ttsProject);

        // tts detail 저장
        if (dto.getTtsDetails() != null) {
            for (TTSDetailDto detailDto : dto.getTtsDetails()) {
                createTTSDetail(detailDto, ttsProject);
            }
        }
        return ttsProject.getId();
    }

    // 프로젝트 업데이트
    @Transactional
    public Long updateProject(TTSSaveDto dto, Long memberId) {

        validateSaveDto(dto);

        // 프로젝트 id로 tts프로젝트 조회
        TTSProject ttsProject = ttsProjectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT));

        // 해당 멤버의 프로젝트가 맞는지 검증
        if (!ttsProject.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.MEMBER_PROJECT_NOT_MATCH);
        }

        VoiceStyle voiceStyle = null;
        // voiceStyleId가 null이 아닌 경우에만 조회 ( voiceStyleId에 null을 허용한다는 의미입니다. 보이스 스타일을 지정하지 않고 저장할 수 있으니까 )
        if (dto.getGlobalVoiceStyleId() != null) {
            voiceStyle = em.merge(voiceStyleRepository.findById(dto.getGlobalVoiceStyleId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_VOICESTYLE)));
        }

        // TTSDetailDto 리스트에 대한 unitSequence 검증
        if (dto.getTtsDetails() != null) {
            validateUnitSequence(dto.getTtsDetails());
        }

        ttsProject.updateTTSProject(
                dto.getProjectName(),
                voiceStyle,
                dto.getFullScript(),
                dto.getGlobalSpeed(),
                dto.getGlobalPitch(),
                dto.getGlobalVolume()
        );

        if (dto.getTtsDetails() != null) {
            for (TTSDetailDto detailDto : dto.getTtsDetails()) {
                // ttsDetail 업데이트 메서드 호출
                processTTSDetail(detailDto, ttsProject);
            }
        }
        return ttsProject.getId();
    }

    private void validateSaveDto(TTSSaveDto dto) {
        // 프로젝트 ID와 디테일 검증
        if (dto.getProjectId() == null) {
            if (dto.getTtsDetails() != null) {
                for (TTSDetailDto detail : dto.getTtsDetails()) {
                    if (detail.getId() != null) {
                        throw new BusinessException(ErrorCode.PROJECT_DETAIL_NOT_MATCH);
                    }
                }
            }
        }
    }


    // ttsDetail 생성 메서드
    private void createTTSDetail(TTSDetailDto detailDto, TTSProject ttsProject) {

        VoiceStyle voiceStyle = null;
        // voiceStyleId가 null이 아닌 경우에만 조회 ( voiceStyleId에 null을 허용한다는 의미입니다. 보이스 스타일을 지정하지 않고 저장할 수 있으니까 )
        if (detailDto.getUnitVoiceStyleId() != null) {
            voiceStyle = em.merge(voiceStyleRepository.findById(detailDto.getUnitVoiceStyleId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_VOICESTYLE)));

        }

        TTSDetail ttsDetail = TTSDetail.createTTSDetail(
                ttsProject,
                detailDto.getUnitScript(),
                detailDto.getUnitSequence()
        );
        ttsDetail.updateTTSDetail(
                voiceStyle,
                detailDto.getUnitScript(),
                detailDto.getUnitSpeed(),
                detailDto.getUnitPitch(),
                detailDto.getUnitVolume(),
                detailDto.getUnitSequence(),
                detailDto.getIsDeleted()
        );

        ttsDetailRepository.save(ttsDetail);
    }

    // ttsDetail 업데이트 메서드
    private void processTTSDetail(TTSDetailDto detailDto, TTSProject ttsProject) {

        VoiceStyle detailStyle = em.merge(voiceStyleRepository.findById(detailDto.getUnitVoiceStyleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT)));

        if (detailDto.getId() != null) {
            // 기존 TTSDetail 업데이트
            TTSDetail ttsDetail = ttsDetailRepository.findById(detailDto.getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT_DETAIL));

            if (!(ttsDetail.getTtsProject().getId().equals(ttsProject.getId()))) {
                throw new BusinessException(ErrorCode.NOT_EXISTS_PROJECT_DETAIL);
            }

            ttsDetail.updateTTSDetail(
                    detailStyle,
                    detailDto.getUnitScript(),
                    detailDto.getUnitSpeed(),
                    detailDto.getUnitPitch(),
                    detailDto.getUnitVolume(),
                    detailDto.getUnitSequence(),
                    detailDto.getIsDeleted()
            );
            ttsDetailRepository.save(ttsDetail);
        } else {
            // 새로운 TTSDetail 생성 메서드 호출
            createTTSDetail(detailDto, ttsProject);
        }
    }

    /**
     * TTSDetailDto 리스트에서 unitSequence 값을 검증하는 메서드. 중복된 unitSequence 값이 없는지, unitSequence가 순차적인지(1, 2, 3, ...) 확인합니다.
     *
     * @param detailDtos TTSDetailDto 리스트
     * @throws BusinessException DUPLICATE_UNIT_SEQUENCE 예외는 unitSequence에 중복이 있을 때 발생 INVALID_UNIT_SEQUENCE_ORDER 예외는
     *                           unitSequence가 순차적이지 않을 때 발생
     */
    private void validateUnitSequence(List<TTSDetailDto> detailDtos) {
        // 중복 체크를 위한 Set 생성
        Set<Integer> unitSequenceSet = new HashSet<>();

        // unitSequence 값 중복 여부 확인
        for (TTSDetailDto detailDto : detailDtos) {
            if (!unitSequenceSet.add(detailDto.getUnitSequence())) {
                // 중복된 unitSequence가 발견되면 예외 발생
                throw new BusinessException(ErrorCode.DUPLICATE_UNIT_SEQUENCE);
            }
        }

        // unitSequence 값을 정렬된 리스트로 변환하여 순차 여부 확인
        List<Integer> sequences = detailDtos.stream()
                .map(TTSDetailDto::getUnitSequence)
                .sorted()
                .collect(Collectors.toList());

        // 정렬된 unitSequence 리스트가 [1, 2, 3, ...] 순서인지 확인
        for (int i = 0; i < sequences.size(); i++) {
            if (sequences.get(i) != i + 1) {
                // 순서가 맞지 않는 경우 예외 발생
                throw new BusinessException(ErrorCode.INVALID_UNIT_SEQUENCE_ORDER);
            }
        }
    }

    // 프로젝트 생성 커스텀 메서드 - 원우
    @Transactional
    public Long createNewProjectCustom(TTSSaveDto dto, Long memberId) {

        validateSaveDto(dto);

        VoiceStyle voiceStyle = null;

        // voiceStyleId가 null이 아닌 경우에만 조회 ( voiceStyleId에 null을 허용한다는 의미입니다. 보이스 스타일을 지정하지 않고 저장할 수 있으니까 )
        if (dto.getGlobalVoiceStyleId() != null) {
            voiceStyle = voiceStyleRepository.findById(dto.getGlobalVoiceStyleId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_VOICESTYLE));
        }


        // 받아온 멤버 id (세션에서) 로 멤버 찾기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // TTSProject 생성
        TTSProject ttsProject = TTSProject.createTTSProject(
                member,
                dto.getProjectName(),
                voiceStyle,
                dto.getFullScript(),
                dto.getGlobalSpeed(),
                dto.getGlobalPitch(),
                dto.getGlobalVolume()
        );
        ttsProject = ttsProjectRepository.save(ttsProject);

        return ttsProject.getId();
    }


    // 프로젝트 업데이트 - 원우
    @Transactional
    public Long updateProjectCustom(TTSSaveDto dto) {
        TTSProject ttsProject = ttsProjectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT));

        VoiceStyle voiceStyle = voiceStyleRepository.findById(dto.getGlobalVoiceStyleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_VOICESTYLE));


        ttsProject.updateTTSProject(
                dto.getProjectName(),
                voiceStyle,
                dto.getFullScript(),
                dto.getGlobalSpeed(),
                dto.getGlobalPitch(),
                dto.getGlobalVolume()
        );

        ttsProjectRepository.save(ttsProject);

        return ttsProject.getId();
    }



    // ttsDetail 생성 커스텀 메서드 - 원우
    public Long createTTSDetailCustom(TTSDetailDto detailDto, TTSProject ttsProject) {
        VoiceStyle voiceStyle = null;

        // voiceStyleId가 null이 아닌 경우에만 조회 ( voiceStyleId에 null을 허용한다는 의미입니다. 보이스 스타일을 지정하지 않고 저장할 수 있으니까 )
        if (detailDto.getUnitVoiceStyleId() != null) {
            voiceStyle = voiceStyleRepository.findById(detailDto.getUnitVoiceStyleId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_VOICESTYLE));
        }

        TTSDetail ttsDetail = TTSDetail.createTTSDetail(
                ttsProject,
                detailDto.getUnitScript(),
                detailDto.getUnitSequence()
        );
        ttsDetail.updateTTSDetail(
                voiceStyle,
                detailDto.getUnitScript(),
                detailDto.getUnitSpeed(),
                detailDto.getUnitPitch(),
                detailDto.getUnitVolume(),
                detailDto.getUnitSequence(),
                detailDto.getIsDeleted()
        );

        ttsDetail = ttsDetailRepository.save(ttsDetail);

        return ttsDetail.getId();
    }

    // ttsDetail 업데이트 커스텀 메서드 - 원우
    public Long processTTSDetailCustom(TTSDetailDto detailDto, TTSProject ttsProject) {
        VoiceStyle detailStyle = voiceStyleRepository.findById(detailDto.getUnitVoiceStyleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT));

        TTSDetail ttsDetail = ttsDetailRepository.findById(detailDto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT_DETAIL));

        ttsDetail.updateTTSDetail(
                detailStyle,
                detailDto.getUnitScript(),
                detailDto.getUnitSpeed(),
                detailDto.getUnitPitch(),
                detailDto.getUnitVolume(),
                detailDto.getUnitSequence(),
                detailDto.getIsDeleted()
        );
        ttsDetailRepository.save(ttsDetail);

        return ttsDetail.getId();
    }

}
