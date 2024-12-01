package com.fourformance.tts_vc_web.service.vc;

import com.fourformance.tts_vc_web.common.constant.APIStatusConst;
import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.*;
import com.fourformance.tts_vc_web.dto.common.GeneratedAudioDto;
import com.fourformance.tts_vc_web.dto.vc.*;
import com.fourformance.tts_vc_web.domain.entity.MemberAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.OutputAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.VCDetail;
import com.fourformance.tts_vc_web.domain.entity.VCProject;
import com.fourformance.tts_vc_web.dto.vc.*;
import com.fourformance.tts_vc_web.repository.*;
import com.fourformance.tts_vc_web.service.common.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VCService_team_multi {
    private final MemberRepository memberRepository;
    private final VCProjectRepository vcProjectRepository;
    private final VCDetailRepository vcDetailRepository;
    private final MemberAudioMetaRepository memberAudioMetaRepository;
    private final S3Service s3Service;

    private final OutputAudioMetaRepository outputAudioMetaRepository;
    private final MemberAudioVCRepository memberAudioVCRepository;

    // VC 프로젝트 상태 조회하기
    @Transactional(readOnly = true)
    public VCProjectResDto getVCProjectDto(Long projectId) {
        // 프로젝트 조회
        VCProject vcProject = vcProjectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT));

        // VCProjectDto로 변환
        VCProjectDto vcPrjDto = VCProjectDto.createVCProjectDto(vcProject);

        Long memberId = vcProject.getMember().getId();

        List<TrgAudioDto> trgAudioDtos = null; // 변경된 구조에 맞는 변수 선언
        if (vcPrjDto.getId() != null && memberId != null) {
            trgAudioDtos = getTrgAudioList(memberId);
        }

        // VCProjectResDto 생성 및 반환
        VCProjectResDto resDto = new VCProjectResDto();
                        resDto.setId(vcPrjDto.getId());
                        resDto.setProjectName(vcPrjDto.getProjectName());
                        resDto.setTrgAudios(trgAudioDtos); // 새로운 구조 반영

        return resDto;
    }

    @Transactional(readOnly = true)
    public List<TrgAudioDto> getTrgAudioList(Long memberId){
        List<MemberAudioMeta> memberAudioMetaList = memberAudioMetaRepository.findByMemberIdAndAudioType(memberId, AudioType.VC_TRG);

        return memberAudioMetaList.stream()
                .filter(meta -> !meta.getIsDeleted())
                .map(meta -> new TrgAudioDto(meta.getId(), meta.getAudioUrl()))
                .toList();
    }



    // VC 프로젝트 상세 값 조회하기
    @Transactional(readOnly = true)
    public List<VCDetailLoadDto> getVCDetailsDto(Long projectId) {
        List<VCDetail> vcDetails = vcDetailRepository.findByVcProject_Id(projectId);

        // isDeleted가 false인 경우에만 VCDetailResDto 목록으로 변환
        return vcDetails.stream()
                .filter(detail -> !detail.getIsDeleted()) // isDeleted가 false인 경우
                .map(this::convertToVCDetailResDto) // VCDetailResDto로 변환
                .collect(Collectors.toList());
    }

    // VCDetail 엔티티를 VCDetailResDto로 변환하는 메서드
    private VCDetailLoadDto convertToVCDetailResDto(VCDetail vcDetail) {

        // 생성된 오디오 url 추가하기
        List<GeneratedAudioDto> audioUrls = outputAudioMetaRepository.findAudioUrlsByVcDetail(vcDetail.getId())
                .stream() // List<OutputAudioMeta>를 Stream으로 변환
                .filter(meta -> meta.getAudioUrl() != null) // audioUrl이 null이 아닌 경우만 필터링
                .map(meta -> new GeneratedAudioDto(meta.getId(), meta.getAudioUrl())) // OutputAudioMeta의 id와 audioUrl을 GeneratedAudioDto로 매핑
                .collect(Collectors.toList()); // Stream 결과를 List<GeneratedAudioDto>로 변환

        String srcUrl = null;

        if(vcDetail.getMemberAudioMeta()!= null && vcDetail.getMemberAudioMeta().getId()!=null){
            srcUrl = memberAudioMetaRepository.findAudioUrlsByAudioMetaIds(vcDetail.getMemberAudioMeta().getId(), AudioType.VC_SRC);
        }

        VCDetailLoadDto resDto = new VCDetailLoadDto();
                       resDto.setId(vcDetail.getId());
                       resDto.setProjectId(vcDetail.getVcProject().getId());
                       resDto.setIsChecked(vcDetail.getIsChecked());
                       resDto.setUnitScript(vcDetail.getUnitScript());
                       resDto.setSrcAudio(srcUrl);
                       resDto.setGenAudios(audioUrls);
        return resDto;
    }

    // VC 프로젝트 TRG 오디오 삭제
    public void deleteTRGAudio(Long trgAudioId){

        // trgVoiceId로 MemberAudioMeta를 조회
        MemberAudioMeta audioMeta = memberAudioMetaRepository.findByIdAndAudioType(trgAudioId, AudioType.VC_TRG);

        // isDeleted = true로 변경
        if (audioMeta == null) { throw new BusinessException(ErrorCode.AUDIO_NOT_FOUND_EXCEPTION); }

        try{

            audioMeta.delete();
            memberAudioMetaRepository.save(audioMeta);

        }catch (Exception e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }

    }

    // VCProject, VCDetail 저장하는 메서드
    public Long saveVCProject(VCSaveDto vcSaveDto, List<MultipartFile> localFiles, Member member) {

        memberAudioMetaRepository.resetSelection(AudioType.VC_TRG);

        // 1. VCProject 생성/업데이트
        VCProject vcProject = vcSaveDto.getProjectId() == null
                ? createNewVCProject(vcSaveDto, member)
                : updateVCProject(vcSaveDto); // member는 안넘겨도 될 것 같음

        // 2. 타겟 파일 처리
        processTrgFiles(vcSaveDto.getTrgFile(), localFiles, vcProject);

        // 3. 소스 파일 처리 (vc detail 처리)
        processSrcFiles(vcSaveDto.getSrcFiles(), localFiles, vcProject);
//        processFiles(vcSaveDto.getSrcFiles(), localFiles, vcProject, AudioType.VC_SRC);

        return vcProject.getId();
    }

    // VCProject 생성, 저장
    private VCProject createNewVCProject(VCSaveDto vcSaveDto, Member member) {

        VCProject vcProject = VCProject.createVCProject(member, vcSaveDto.getProjectName());
        vcProjectRepository.save(vcProject);
        return vcProject;
    }

    //VCProject 업데이트
    private VCProject updateVCProject(VCSaveDto vcSaveDto) {
        VCProject vcProject = vcProjectRepository.findById(vcSaveDto.getProjectId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_PROJECT));

        vcProject.updateVCProject(vcSaveDto.getProjectName(), null);

        return vcProject;
    }

//    private void processFiles(List<? extends AudioFileDto> fileDtos, List<MultipartFile> files, VCProject vcProject, AudioType audioType) {
//
//        if (fileDtos == null || fileDtos.isEmpty()) { // 업로드 된 파일이 없을 때
//            return;
//        }
//
//        for (AudioFileDto fileDto : fileDtos) {
//            MemberAudioMeta audioMeta = null;
//
//            // trg audio면 s3에서 목록 선택할 수 있음
//            if (fileDto instanceof TrgAudioFileDto) {
//                // S3 파일 처리
//                Long s3Id = ((TrgAudioFileDto) fileDto).getS3MemberAudioMetaId();
//                if (s3Id != null) {
//                    audioMeta = memberAudioMetaRepository.findById(s3Id)
//                            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_AUDIO));
//                }
//            }
//            // 로컬 파일 처리
//            String localFileName = fileDto.getLocalFileName();
//
//            if (localFileName != null) {
//                MultipartFile localFile = findMultipartFileByName(files, localFileName);
//                String uploadUrl = s3Service.uploadAndSaveMemberFile(localFile,vcProject.getMember().getId(), vcProject.getId(), audioType);
//
//
//                audioMeta = memberAudioMetaRepository.findFirstByAudioUrl(uploadUrl)
//                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_AUDIO));
//                memberAudioMetaRepository.selectAudio(audioMeta.getId(), AudioType.VC_TRG);
//            }
//
//
//            if (audioMeta == null) {
//                throw new BusinessException(ErrorCode.INVALID_PROJECT_DATA);
//            }
//
//            if (audioType == AudioType.VC_TRG) {
//                // 타겟 파일은 VCProject에 저장
//                vcProject.updateVCProject(vcProject.getProjectName(), audioMeta);
//            } else {
//                // 소스 파일은 VCDetail에 저장
//                VCDetail vcDetail = VCDetail.createVCDetail(vcProject, audioMeta);
//                SrcAudioFileDto srcFile = (SrcAudioFileDto) fileDto;
//                vcDetail.updateDetails(srcFile.getIsChecked(), srcFile.getUnitScript());
//                vcDetailRepository.save(vcDetail);
//            }
//        }
//    }

    private void processSrcFiles(List<SrcAudioFileDto> fileDtos, List<MultipartFile> files, VCProject vcProject) {
        // detail id가 존재하면 for문에서 다음으로 넘어가기
        // detail id가 null이면
        // src오디오는 중복으로 들어갈 수가 없다 검증 체크
        // detail id도 받고 localfilename도 받아야할까? => 같이 있으면 오디오 중복 검증을 할 수 있다

        if (fileDtos == null || fileDtos.isEmpty()) { // 업로드 된 파일이 없을 때
            return;
        }

        for (SrcAudioFileDto fileDto : fileDtos) {
            MemberAudioMeta audioMeta = null;

            // detailId가 존재하면 해당 VCDetail을 조회하여 업데이트
            if(fileDto.getDetailId()!=null){
                VCDetail vcDetail = vcDetailRepository.findById(fileDto.getDetailId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.VC_DETAIL_NOT_FOUND));

                vcDetail.updateDetails(fileDto.getIsChecked(), fileDto.getUnitScript());
                vcDetailRepository.save(vcDetail);
                continue;
            }else {
                // detailId가 없으면 vcDetail 생성
                String localFileName = fileDto.getLocalFileName();

                if (localFileName != null) {
                    MultipartFile localFile = findMultipartFileByName(files, localFileName);

                    // S3 업로드 및 URL 저장
                    String uploadUrl = s3Service.uploadAndSaveMemberFile(
                            localFile,
                            vcProject.getMember().getId(),
                            vcProject.getId(),
                            AudioType.VC_SRC
                    );

                    // 업로드된 파일을 통해 MemberAudioMeta 검색
                    audioMeta = memberAudioMetaRepository.findFirstByAudioUrl(uploadUrl)
                            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_AUDIO));
                    memberAudioMetaRepository.selectAudio(audioMeta.getId(), AudioType.VC_TRG);
                }

                if (audioMeta == null) {
                    throw new BusinessException(ErrorCode.INVALID_PROJECT_DATA);
                }

                // 소스 파일은 VCDetail에 저장
                VCDetail vcDetail = VCDetail.createVCDetail(vcProject, audioMeta);
                vcDetail.updateDetails(fileDto.getIsChecked(), fileDto.getUnitScript());
                vcDetailRepository.save(vcDetail);
            }
        }
    }
    private void processTrgFiles(TrgAudioFileDto fileDto, List<MultipartFile> files, VCProject vcProject) {

        MemberAudioMeta audioMeta = null;

        // 업로드 된 타켓 파일이 없을 때
        if (fileDto == null) {
            vcProject.injectTargetAudioMeta(null);
            return;
        }

        // S3 파일 처리
        Long s3Id = fileDto.getS3MemberAudioMetaId();

        if (s3Id != null) {
            audioMeta = memberAudioMetaRepository.findById(s3Id)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_AUDIO));


        } else{
            // 로컬 파일 처리
            String localFileName = fileDto.getLocalFileName();

            if (localFileName != null) {
                MultipartFile localFile = findMultipartFileByName(files, localFileName);
                String uploadUrl = s3Service.uploadAndSaveMemberFile(localFile,vcProject.getMember().getId(), vcProject.getId(), AudioType.VC_TRG);


                audioMeta = memberAudioMetaRepository.findFirstByAudioUrl(uploadUrl)
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_AUDIO));
                memberAudioMetaRepository.selectAudio(audioMeta.getId(), AudioType.VC_TRG);
            }
        }
//        if (audioMeta == null) {
//            throw new BusinessException(ErrorCode.INVALID_PROJECT_DATA);
//        }

        // 타겟 파일은 VCProject에 저장
        vcProject.injectTargetAudioMeta(audioMeta);
    }

    private MultipartFile findMultipartFileByName(List<MultipartFile> files, String localFileName) {
        return files.stream()
                .filter(file -> file.getOriginalFilename().equals(localFileName))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_PROCESSING_ERROR));
    }

    private MultipartFile findMultipartFileByName(MultipartFile file, String localFileName) {
        if (file.getOriginalFilename().equals(localFileName)) {
            return file;
        } else {
            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }

}
