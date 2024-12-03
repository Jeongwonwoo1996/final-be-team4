package com.fourformance.tts_vc_web.service.common;

import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.MemberAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.OutputAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.Project;
import com.fourformance.tts_vc_web.domain.entity.TTSDetail;
import com.fourformance.tts_vc_web.domain.entity.VCDetail;
import com.fourformance.tts_vc_web.repository.MemberAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.MemberAudioVCRepository;
import com.fourformance.tts_vc_web.repository.OutputAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.ProjectRepository;
import com.fourformance.tts_vc_web.repository.TTSDetailRepository;
import com.fourformance.tts_vc_web.repository.VCDetailRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService_team_multi {

    private final ProjectRepository projectRepository;
    private final TTSDetailRepository ttsDetailRepository;
    private final VCDetailRepository vcDetailRepository;
    private final OutputAudioMetaRepository outputAudioMetaRepository;
    private final MemberAudioVCRepository memberAudioVCRepository;
    private final MemberAudioMetaRepository memberAudioMetaRepository;


    /**
     * TTS 프로젝트 삭제 메서드
     * <p>
     * TTS 프로젝트, TTS Detail, 생성된 오디오 데이터들을 삭제합니다. (isDeleted = true)
     *
     * @param projectId : 프로젝트 ID
     * @throws BusinessException NOT_EXISTS_PROJECT : 프로젝트 ID가 없을 때 발생 SERVER_ERROR       : 내부 코드 에러일 때 발생
     */
    @Transactional
    public void deleteTTSProject(Long projectId) {

        // 1. 프로젝트 조회 및 isDeleted 설정
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT));

        project.deletedAt(); // deletedAt 설정
        projectRepository.save(project);

        try {
            // 2. TTSDetail 조회 및 isDeleted 설정
            List<TTSDetail> ttsDetails = ttsDetailRepository.findByTtsProject_Id(projectId);
            for (TTSDetail detail : ttsDetails) {
                detail.deleteTTSDetail();
                ttsDetailRepository.save(detail);
            }

            // 3. TTS 생성된 오디오 조회 및 isDeleted 설정
            // TTSDetail ID 리스트 추출
            List<Long> ttsDetailIds = ttsDetails.stream()
                    .map(TTSDetail::getId)
                    .toList();

            // OutputAudioMeta 삭제 처리
            List<OutputAudioMeta> outputAudio = outputAudioMetaRepository.findByTtsDetailAndIsDeletedFalse(
                    ttsDetailIds);
            for (OutputAudioMeta audio : outputAudio) {
                audio.deleteOutputAudioMeta();
                outputAudioMetaRepository.save(audio);
            }

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }

    }

    // TTS 선택된 모든 항목 삭제
    @Transactional
    public void deleteTTSDetail(Long projectId, List<Long> ttsDetailIdList) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT));

        if (project == null || project.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_EXISTS_PROJECT);
        }

        // TTSDetail ID 리스트를 사용하여 TTSDetail 엔티티를 조회
        List<TTSDetail> ttsDetails = ttsDetailRepository.findByTtsDetailIds(ttsDetailIdList);

        // TTSDetail ID 리스트 추출
        List<Long> ttsDetailIds = ttsDetails.stream()
                .map(TTSDetail::getId)
                .toList();

        if (ttsDetailIdList.size() != ttsDetailIds.size()) {
            throw new BusinessException(ErrorCode.NOT_EXISTS_PROJECT_DETAIL);
        }

        try {

            // 1. TTSDetail 삭제 처리
            for (TTSDetail detail : ttsDetails) {
                detail.deleteTTSDetail();
                ttsDetailRepository.save(detail);
            }

            // 2. OutputAudioMeta 삭제 처리
            List<OutputAudioMeta> outputAudio = outputAudioMetaRepository.findByTtsDetailAndIsDeletedFalse(
                    ttsDetailIds);
            for (OutputAudioMeta audio : outputAudio) {
                audio.deleteOutputAudioMeta();
                outputAudioMetaRepository.save(audio);
            }

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }

    // TTS, VC 선택된 오디오 모든 항목 삭제
    @Transactional
    public void deleteAudioIds(List<Long> audioIds) {

        // OutputAudioMeta 삭제 처리
        try {
            List<OutputAudioMeta> outputAudio = outputAudioMetaRepository.findByIds(audioIds);

            for (OutputAudioMeta audio : outputAudio) {
                audio.deleteOutputAudioMeta();
                outputAudioMetaRepository.save(audio);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }


    /**
     * VC 프로젝트 삭제 메서드
     * <p>
     * VC 프로젝트, VC Detail, 생성된 오디오, Src 오디오 데이터들을 삭제합니다. (isDeleted = true)
     *
     * @param projectId : 프로젝트 ID
     * @throws BusinessException NOT_EXISTS_PROJECT : 프로젝트 ID가 없을 때 발생 SERVER_ERROR       : 내부 코드 에러일 때 발생
     */
    @Transactional
    public void deleteVCProject(Long projectId) {

        // 1. 프로젝트 조회 및 isDeleted 설정
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT));

        project.deletedAt(); // deletedAt 설정
        projectRepository.save(project);

        try {
            // 2. VCDetail 조회 및 isDeleted 설정
            List<VCDetail> vcDetails = vcDetailRepository.findByVcProject_Id(projectId);
            for (VCDetail detail : vcDetails) {
                detail.markAsDeleted();
                vcDetailRepository.save(detail);
            }

            // 3. VC 생성된 오디오 조회 및 isDeleted 설정
            // TTSDetail ID 리스트 추출
            List<Long> vcDetailIds = vcDetails.stream()
                    .map(VCDetail::getId)
                    .toList();

            List<OutputAudioMeta> outputAudio = outputAudioMetaRepository.findByTtsDetailAndIsDeletedFalse(vcDetailIds);
            for (OutputAudioMeta audio : outputAudio) {
                audio.deleteOutputAudioMeta();
                outputAudioMetaRepository.save(audio);
            }

            // 4. VC src 오디오 isDeleted 설정
            List<Long> memberAudioIds = vcDetailRepository.findMemberAudioIdsByVcDetailIds(vcDetailIds);
            List<MemberAudioMeta> memberAudioList = memberAudioMetaRepository.findByMemberAudioIds(memberAudioIds,
                    AudioType.VC_SRC);

            for (MemberAudioMeta memberAudioMeta : memberAudioList) {
                memberAudioMeta.delete(); // isDeleted = true, deletedAt 설정
                memberAudioMetaRepository.save(memberAudioMeta); // 업데이트 저장
            }

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }

    }

    // VC 선택된 모든 항목 삭제
    @Transactional
    public void deleteVCDetail(List<Long> vcDetailIdList) {

        // VCDetail ID 리스트를 사용하여 VCDetail 엔티티를 조회
        List<VCDetail> vcDetails = vcDetailRepository.findByVcDetailIds(vcDetailIdList);

        // VCDetail ID 리스트 추출
        List<Long> vcDetailIds = vcDetails.stream()
                .map(VCDetail::getId)
                .toList();

        if (vcDetailIdList.size() != vcDetailIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_PROJECT_ID);
        }

        try {

            // 1. VCDetail 삭제 처리
            for (VCDetail detail : vcDetails) {
                detail.markAsDeleted();
                vcDetailRepository.save(detail);
            }

            // 2. OutputAudioMeta 삭제 처리
            List<OutputAudioMeta> outputAudio = outputAudioMetaRepository.findByVcDetailAndIsDeletedFalse(vcDetailIds);
            for (OutputAudioMeta audio : outputAudio) {
                audio.deleteOutputAudioMeta();
                outputAudioMetaRepository.save(audio);
            }

            // 3. MemberAudioMeta 에서 AudioType이 VC_SRC 인 것 삭제 처리
            List<Long> memberAudioIds = vcDetailRepository.findMemberAudioIdsByVcDetailIds(vcDetailIds);
            List<MemberAudioMeta> memberAudioList = memberAudioMetaRepository.findByMemberAudioIds(memberAudioIds,
                    AudioType.VC_SRC);

            for (MemberAudioMeta memberAudioMeta : memberAudioList) {
                memberAudioMeta.delete(); // isDeleted = true, deletedAt 설정
                memberAudioMetaRepository.save(memberAudioMeta); // 업데이트 저장
            }


        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }

}