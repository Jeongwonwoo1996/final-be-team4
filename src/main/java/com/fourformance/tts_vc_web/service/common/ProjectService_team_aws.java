package com.fourformance.tts_vc_web.service.common;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.ConcatDetail;
import com.fourformance.tts_vc_web.domain.entity.MemberAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.OutputAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.Project;
import com.fourformance.tts_vc_web.repository.ConcatDetailRepository;
import com.fourformance.tts_vc_web.repository.MemberAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.OutputAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.ProjectRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService_team_aws {

    private final ProjectRepository projectRepository;
    private final ConcatDetailRepository concatDetailRepository;
    private final OutputAudioMetaRepository outputAudioMetaRepository;
    private final MemberAudioMetaRepository memberAudioMetaRepository;

    // 프로젝트 삭제 컬럼 업데이트
    @Transactional
    public void deleteProject(Long projectId) {

        // 1. 프로젝트 조회 및 isDeleted 설정
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT));

        project.deletedAt(); // deletedAt 설정
        projectRepository.save(project);

        try {
            // 2. ConcatDetail 조회 및 isDeleted 설정
            List<ConcatDetail> concatDetails = concatDetailRepository.findByConcatProject_Id(projectId);
            for (ConcatDetail detail : concatDetails) {
                // ConcatDetail 삭제
                detail.deleteConcatDetail();
                concatDetailRepository.save(detail);

                // 연결된 MemberAudioMeta 삭제 처리
                MemberAudioMeta memberAudioMeta = detail.getMemberAudioMeta();
                if (memberAudioMeta != null) {
                    memberAudioMeta.delete(); // isDeleted=true, deletedAt 설정
                    memberAudioMetaRepository.save(memberAudioMeta);
                }
            }

            // 3. Concat 생성된 오디오 조회 및 isDeleted 설정
            List<OutputAudioMeta> outputAudio = outputAudioMetaRepository.findAudioUrlsByConcatProject(projectId);
            for (OutputAudioMeta audio : outputAudio) {
                audio.deleteOutputAudioMeta();
                outputAudioMetaRepository.save(audio);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }
    // 1. 프로젝트 조회 및 isDeleted 설정


    // Concat 선택된 모든 항목 삭제
    @Transactional
    public void deleteSelectedDetails(List<Long> concatDetailIdList) {

        // ConcatDetail Id 리스트를 사용하여 ConcatDetail 엔티티를 조회
        List<ConcatDetail> concatDetails = concatDetailRepository.findByIdIn(concatDetailIdList);

        // ConcatDetail ID 리스트 추출
        List<Long> concatDetailIds = concatDetails.stream()
                .map(ConcatDetail::getId)
                .toList();

        if (concatDetailIdList.size() != concatDetailIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_PROJECT_ID);
        }

        try {

            // 1. ConcatDetail 삭제 처리
            for (ConcatDetail detail : concatDetails) {
                detail.deleteConcatDetail();
                concatDetailRepository.save(detail);
            }

            // 2. MemberAudioMeta 삭제 처리
            List<MemberAudioMeta> outputAudio = memberAudioMetaRepository.findByConcatDetailIds(concatDetailIds);
            for (MemberAudioMeta meta : outputAudio) {
                meta.delete();
                memberAudioMetaRepository.save(meta);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }

    }

//    public List<ProjectListDto> getProjects(Long memberId, String keyword) {
//        return projectRepository.findProjectsBySearchCriteria(memberId, keyword);
//    }

    // Concat 선택된 오디오 모든 항목 삭제
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


}
