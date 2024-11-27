package com.fourformance.tts_vc_web.service.common;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.common.constant.ProjectType;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.ConcatDetail;
import com.fourformance.tts_vc_web.domain.entity.ConcatProject;
import com.fourformance.tts_vc_web.domain.entity.Member;
import com.fourformance.tts_vc_web.domain.entity.MemberAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.OutputAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.Project;
import com.fourformance.tts_vc_web.domain.entity.TTSDetail;
import com.fourformance.tts_vc_web.domain.entity.TTSProject;
import com.fourformance.tts_vc_web.domain.entity.VCDetail;
import com.fourformance.tts_vc_web.domain.entity.VCProject;
import com.fourformance.tts_vc_web.repository.ConcatDetailRepository;
import com.fourformance.tts_vc_web.repository.ConcatProjectRepository;
import com.fourformance.tts_vc_web.repository.MemberAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import com.fourformance.tts_vc_web.repository.OutputAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.ProjectRepository;
import com.fourformance.tts_vc_web.repository.TTSDetailRepository;
import com.fourformance.tts_vc_web.repository.VCDetailRepository;
import com.fourformance.tts_vc_web.repository.VCProjectRepository;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final OutputAudioMetaRepository outputAudioMetaRepository;
    private final TTSDetailRepository ttsDetailRepository;
    private final VCDetailRepository vcDetailRepository;
    private final ConcatProjectRepository concatProjectRepository;
    private final AmazonS3Client amazonS3Client;
    private final ProjectRepository projectRepository;
    private final MemberAudioMetaRepository memberAudioMetaRepository;
    private final MemberRepository memberRepository;
    private final VCProjectRepository vcProjectRepository;
    private final ConcatDetailRepository concatDetailRepository;

    private final AmazonS3 amazonS3;

    // TTS와 VC로 반환한 유닛 오디오를 S3 버킷에 저장
    public String uploadUnitSaveFile(MultipartFile file, Long userId, Long projectId, Long detailId) {

        try {
            // 파일이 비어 있는지 확인
            if (file.isEmpty()) {
                throw new BusinessException(ErrorCode.EMPTY_FILE);
            }

            // Project의 실제 타입에 따라 ProjectType 설정
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

            ProjectType projectType;
            String fileName;

            // 오디오파일 이름으로 사용할 날짜 포맷 지정
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timeStamp = sdf.format(new Date());

            if (project instanceof TTSProject) {
                projectType = ProjectType.TTS;
                fileName =
                        "Generated/" + userId + "/" + projectType + "/" + projectId + "/" + detailId + "/" + timeStamp
                                + ".wav";
            } else if (project instanceof VCProject) {
                projectType = ProjectType.VC;
                fileName =
                        "Generated/" + userId + "/" + projectType + "/" + projectId + "/" + detailId + "/" + timeStamp
                                + ".mp3";
            } else {
                throw new BusinessException(ErrorCode.UNSUPPORTED_PROJECT_TYPE);
            }

            // 메타데이터 저장
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // S3에 파일 업로드 (전체 경로 포함)
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

            // 파일의 전체 URL 반환
            String fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();

            // 반환된 오디오 메타를 DB에 저장
            saveTTSOrVCOutputAudioMeta(fileName, detailId, projectType, fileUrl);

            return fileUrl;

        } catch (AmazonClientException e) {
            // S3 업로드 중 발생하는 예외
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED);
        } catch (IOException e) {
            // 파일 처리 중 발생하는 예외
            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }

    // Concat 기능을 수행해서 반환한 오디오를 S3 버킷에 저장
    public String uploadConcatSaveFile(MultipartFile file, Long userId, Long projectId) {

        try {
            // 파일이 비어 있는지 확인
            if (file.isEmpty()) {
                throw new BusinessException(ErrorCode.EMPTY_FILE);
            }

            // 오디오파일 이름으로 사용할 날짜 포맷 지정
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timeStamp = sdf.format(new Date());

            // 전체 경로를 포함한 파일 이름 설정
            String fileName = "Generated/" + userId + "/CONCAT" + "/" + projectId + "/" + timeStamp + ".wav";

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // S3에 파일 업로드 (전체 경로 포함)
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

            // 파일의 전체 URL 반환
            String fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();

            // 반환된 오디오 메타를 DB에 저장
            saveConcatAudioMeta(fileName, projectId, fileUrl);

            return fileUrl;

        } catch (AmazonClientException e) {
            // S3 업로드 중 발생하는 예외
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED);
        } catch (IOException e) {
            // 파일 처리 중 발생하는 예외
            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }

    // DB에 TTS와 VC에 대한 오디오 메타를 저장
    public OutputAudioMeta saveTTSOrVCOutputAudioMeta(String fileName, Long detailId, ProjectType projectType,
                                                      String audioUrl) {

        TTSDetail ttsDetail;
        VCDetail vcDetail;

        // TTS 프로젝트인지 VC 프로젝트인지 판별
        if (projectType.equals(ProjectType.TTS)) {
            ttsDetail = ttsDetailRepository.findById(detailId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.DETAIL_NOT_FOUND));
            vcDetail = null;
        } else if (projectType.equals(ProjectType.VC)) {
            vcDetail = vcDetailRepository.findById(detailId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.DETAIL_NOT_FOUND));
            ttsDetail = null;
        } else {
            throw new BusinessException(ErrorCode.UNSUPPORTED_PROJECT_TYPE);
        }

        // OutputAudioMeta 객체 생성
        OutputAudioMeta outputAudioMeta = OutputAudioMeta.createOutputAudioMeta(fileName, ttsDetail, vcDetail, null,
                projectType, audioUrl);

        // 생성한 OutputAudioMeta를 DB에 저장
        return outputAudioMetaRepository.save(outputAudioMeta);
    }

    // DB에 Concat 기능을 수행해서 반환한 오디오 메타를 저장
    public OutputAudioMeta saveConcatAudioMeta(String fileName, Long projectId, String audioUrl) {
        ConcatProject concatProject = concatProjectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        // OutputAudioMeta 객체 생성
        OutputAudioMeta outputAudioMeta = OutputAudioMeta.createOutputAudioMeta(fileName, null, null, concatProject,
                ProjectType.CONCAT, audioUrl);

        // 생성한 OutputAudioMeta를 DB에 저장
        return outputAudioMetaRepository.save(outputAudioMeta);
    }

    // 다운로드 받을 오디오의 버킷 URL을 제공하는 메서드
    public String generatePresignedUrl(String bucketRoute) {
        try {
            // presigned url 생성
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, bucketRoute);
            request.withMethod(com.amazonaws.HttpMethod.GET)
                    .withExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5));
            URL presignedUrl = amazonS3Client.generatePresignedUrl(request);

            return presignedUrl.toString();
        } catch (AmazonClientException e) {
            // Presigned URL 생성 실패 예외
            throw new BusinessException(ErrorCode.S3_PRESIGNED_URL_FAILED);
        }
    }

//    // 유저 오디오를 S3에 업로드하고 DB에 저장하는 메서드
//    public List<String> uploadAndSaveMemberFile(List<MultipartFile> files, Long memberId, Long projectId,
//                                                AudioType audioType, String voiceId) {
//
//        try {
//            // url을 담을 리스트
//            List<String> uploadedUrls = new ArrayList<>();
//            Project project = projectRepository.findById(projectId)
//                    .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));
//            Member member = memberRepository.findById(memberId)
//                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
//
//            // 개별 파일 url을 List로 저장.
//            for (MultipartFile file : files) {
//                if (file.isEmpty()) {
//                    throw new BusinessException(ErrorCode.EMPTY_FILE);
//                }
//
//                String originFilename = Normalizer.normalize(file.getOriginalFilename(), Normalizer.Form.NFC);
//                String filename = "member/" + memberId + "/" + audioType + "/" + projectId + "/" + originFilename;
//
//                ObjectMetadata metadata = new ObjectMetadata();
//                metadata.setContentType(file.getContentType());
//                metadata.setContentLength(file.getSize());
//
//                // 버킷에 업로드
//                amazonS3Client.putObject(bucket, filename, file.getInputStream(), metadata);
//                String fileUrl = amazonS3Client.getUrl(bucket, filename).toString();
//
//                // url 리스트에 추가
//                uploadedUrls.add(fileUrl);
//
//                // 오디오 메타 객체 생성 및 DB 저장
//                String finalVoiceId = (audioType == AudioType.VC_TRG) ? voiceId : null;
//                MemberAudioMeta memberAudioMeta = MemberAudioMeta.createMemberAudioMeta(member, filename, fileUrl,
//                        audioType, finalVoiceId);
//                memberAudioMetaRepository.save(memberAudioMeta);
//
//            }
//
//            return uploadedUrls;
//
//        } catch (AmazonClientException e) {
//            // S3 업로드 중 발생하는 예외
//            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED);
//        } catch (IOException e) {
//            // 파일 처리 중 발생하는 예외
//            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
//        }
//    }

    // 유저 오디오를 S3에 업로드하고 DB에 저장하는 메서드
    public List<String> uploadAndSaveMemberFile(List<MultipartFile> files, Long memberId, Long projectId,
                                                AudioType audioType) {

        try {
            // url을 담을 리스트
            List<String> uploadedUrls = new ArrayList<>();
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

            // 파일과 디테일 처리
            if (audioType.equals(AudioType.VC_SRC)) {
                handleVCSrcFiles(files, projectId, member, uploadedUrls);
            } else if (audioType.equals(AudioType.CONCAT)) {
                handleConcatFiles(files, projectId, member, uploadedUrls);
            } else if (audioType.equals(AudioType.VC_TRG)) {
                handleVCTrgFiles(files, projectId, member, uploadedUrls);
            } else {
                throw new BusinessException(ErrorCode.UNSUPPORTED_AUDIO_TYPE);
            }

            return uploadedUrls;

        } catch (AmazonClientException e) {
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }

    private void handleVCSrcFiles(List<MultipartFile> files, Long projectId, Member member, List<String> uploadedUrls)
            throws IOException {
        List<VCDetail> vcDetails = vcDetailRepository.findAllByProjectId(projectId); // VC 디테일 가져오기
//        if (vcDetails.size() != files.size()) {
//            throw new BusinessException(ErrorCode.DETAIL_MISMATCH); // 디테일 수와 파일 수가 다름
//        }

        int index = 0;
        for (MultipartFile file : files) {
            String fileUrl = uploadFileToS3(file, member.getId(), projectId, AudioType.VC_SRC);
            uploadedUrls.add(fileUrl);

            MemberAudioMeta memberAudioMeta = saveMemberAudioMeta(member, file, fileUrl, AudioType.VC_SRC);

            // VCDetail 업데이트
            VCDetail vcDetail = vcDetails.get(index++);
            vcDetail.injectLocalAudio(memberAudioMeta);
            vcDetailRepository.save(vcDetail);
        }
    }

    private void handleConcatFiles(List<MultipartFile> files, Long projectId, Member member,
                                   List<String> uploadedUrls) throws IOException {
        List<ConcatDetail> concatDetails = concatDetailRepository.findAllByProjectId(projectId); // CONCAT 디테일 가져오기
//        if (concatDetails.size() != files.size()) {
//            throw new BusinessException(ErrorCode.DETAIL_MISMATCH); // 디테일 수와 파일 수가 다름
//        }

        int index = 0;
        for (MultipartFile file : files) {
            String fileUrl = uploadFileToS3(file, member.getId(), projectId, AudioType.CONCAT);
            uploadedUrls.add(fileUrl);

            MemberAudioMeta memberAudioMeta = saveMemberAudioMeta(member, file, fileUrl, AudioType.CONCAT);

            // ConcatDetail 업데이트
            ConcatDetail concatDetail = concatDetails.get(index++);
            concatDetail.injectMemberAudioMeta(memberAudioMeta);
            concatDetailRepository.save(concatDetail);
        }
    }

    private void handleVCTrgFiles(List<MultipartFile> files, Long projectId, Member member, List<String> uploadedUrls)
            throws IOException {
        if (files.size() != 1) {
            throw new BusinessException(ErrorCode.INVALID_FILE_COUNT); // VC_TRG는 단일 파일만 허용
        }

        MultipartFile file = files.get(0);
        String fileUrl = uploadFileToS3(file, member.getId(), projectId, AudioType.VC_TRG);
        uploadedUrls.add(fileUrl);

        MemberAudioMeta memberAudioMeta = saveMemberAudioMeta(member, file, fileUrl, AudioType.VC_TRG);

        // VCProject 업데이트
        VCProject vcProject = vcProjectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));
        vcProject.injectTargetAudioMeta(memberAudioMeta);
        vcProjectRepository.save(vcProject);
    }

    private String uploadFileToS3(MultipartFile file, Long memberId, Long projectId, AudioType audioType)
            throws IOException {
        String originFilename = Normalizer.normalize(file.getOriginalFilename(), Normalizer.Form.NFC);
        String filename = "member/" + memberId + "/" + audioType + "/" + projectId + "/" + originFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3Client.putObject(bucket, filename, file.getInputStream(), metadata);
        return amazonS3Client.getUrl(bucket, filename).toString();
    }

    private MemberAudioMeta saveMemberAudioMeta(Member member, MultipartFile file, String fileUrl,
                                                AudioType audioType) {
        MemberAudioMeta memberAudioMeta = MemberAudioMeta.createMemberAudioMeta(
                member, file.getOriginalFilename(), fileUrl, audioType
        );
        return memberAudioMetaRepository.save(memberAudioMeta);
    }


    public String uploadAndSaveMemberFile(MultipartFile file, Long memberId, Long projectId,
                                          AudioType audioType) {

        try {
            if (file.isEmpty()) {
                throw new BusinessException(ErrorCode.EMPTY_FILE);
            }

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

            String originFilename = Normalizer.normalize(file.getOriginalFilename(), Normalizer.Form.NFC);
            String filename = "member/" + memberId + "/" + audioType + "/" + projectId + "/" + originFilename;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // S3 버킷에 업로드
            amazonS3Client.putObject(bucket, filename, file.getInputStream(), metadata);
            String fileUrl = amazonS3Client.getUrl(bucket, filename).toString();

            // 오디오 메타 객체 생성 및 DB 저장
            MemberAudioMeta memberAudioMeta = MemberAudioMeta.createMemberAudioMeta(member, filename, fileUrl,
                    audioType);
            memberAudioMetaRepository.save(memberAudioMeta);

            return fileUrl;

        } catch (AmazonClientException e) {
            // S3 업로드 중 발생하는 예외
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED);
        } catch (IOException e) {
            // 파일 처리 중 발생하는 예외
            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }

    // ================================= 버킷 오디오 삭제 구현중 =============================================


    public void deleteAudioPerProject(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        String projectType = null;

        if (project instanceof TTSProject) {
            projectType = "TTS";
        } else if (project instanceof VCProject) {
            projectType = "VC";
        } else if (project instanceof ConcatProject) {
            projectType = "CONCAT";
        }

        Long memberId = project.getMember().getId();

        String vcTrgAudioRoute = "member/" + memberId + "/" + "VC_TRG" + "/" + projectId;
        String vcSRCAudioRoute = "member/" + memberId + "/" + "VC_SRC" + "/" + projectId;
        String concatAudioRoute = "member/" + memberId + "/" + "CONCAT" + "/" + projectId;

        String outputAudioRoute = "Generated/" + memberId + "/" + projectType + "/" + projectId + "/";

        if (project instanceof TTSProject) {
            deleteDirectoryFromS3(outputAudioRoute);
        } else if (project instanceof VCProject) {
            deleteDirectoryFromS3(vcTrgAudioRoute);
            deleteDirectoryFromS3(vcSRCAudioRoute);
            deleteDirectoryFromS3(outputAudioRoute);
        } else if (project instanceof ConcatProject) {
            deleteDirectoryFromS3(concatAudioRoute);
            deleteDirectoryFromS3(outputAudioRoute);
        }

        // 멤버오디오메타디비 업데이트
        memberAudioMetaUpdate(projectId);

        // 아웃풋오디오메타딥 업데이트
        outputAudioMetaUpdate(projectId);
    }

    // DB update로직
    private void outputAudioMetaUpdate(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        // 프로젝트에서 OutputAudioMeta를 찾아서 OutputAudioMeta의 isDeleted업뎃 + 삭제시간업뎃
        List<OutputAudioMeta> outputAudioMetaByProjectId = outputAudioMetaRepository.findOutputAudioMetaByAnyProjectId(
                projectId);
        for (OutputAudioMeta outputAudioMeta : outputAudioMetaByProjectId) {                    // 업데이트 치고, 저장.
            outputAudioMeta.deleteOutputAudioMeta();
            outputAudioMetaRepository.save(outputAudioMeta);
        }
    }

    private void memberAudioMetaUpdate(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        if (project instanceof TTSProject) {
            return;

            // VC 프로젝트일 때
        } else if (project instanceof VCProject) {

            // 1. 해당 VC 프로젝트에 일치하는 타겟 오디오에 대한 memberAudioMeta를 찾아서 isDeleted를 1로 업데이트
            Long memberAudioMetaId = memberAudioMetaRepository.findTargetAudioMetaIdByVCProjectId(projectId);
            System.out.println("========================   memberAudioMetaId = " + memberAudioMetaId);
            if (memberAudioMetaId != null) {
                MemberAudioMeta memberAudioMeta = memberAudioMetaRepository.findById(memberAudioMetaId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_AUDIO_META_NOT_FOUND));
                memberAudioMeta.delete();
                memberAudioMetaRepository.save(memberAudioMeta);
            }

            // 2. 해당 VC 프로젝트에 일치하는 소스 오디오들에 대한 memberAudioMeta를 찾아서 isDeleted를 1로 업데이트
            List<Long> memberAudioMetaIds = memberAudioMetaRepository.findSourceAudioMetaIdsByVCProjectId(projectId);
            if (memberAudioMetaIds != null && !memberAudioMetaIds.isEmpty()) {
                for (Long sourceAudioMetaId : memberAudioMetaIds) {
                    System.out.println("======================  sourceAudioMetaId = " + sourceAudioMetaId);
                    if (sourceAudioMetaId != null) {
                        MemberAudioMeta meta = memberAudioMetaRepository.findById(sourceAudioMetaId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_AUDIO_META_NOT_FOUND));
                        meta.delete();
                        memberAudioMetaRepository.save(meta);
                    }
                }
            }

        } else if (project instanceof ConcatProject) {
            // Concat 프로젝트일 때
            List<Long> memberAudioMetaIds = memberAudioMetaRepository.findMemberAudioMetaIdsByConcatProjectId(
                    projectId);
            if (memberAudioMetaIds != null && !memberAudioMetaIds.isEmpty()) {
                for (Long memberAudioMetaId : memberAudioMetaIds) {
                    System.out.println("======================  memberAudioMetaId = " + memberAudioMetaId);
                    if (memberAudioMetaId != null) {
                        MemberAudioMeta meta = memberAudioMetaRepository.findById(memberAudioMetaId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_AUDIO_META_NOT_FOUND));
                        meta.delete();
                        memberAudioMetaRepository.save(meta);
                    }
                }
            }
        }
    }

    // == 유닛 삭제 구현중 == == 유닛 삭제 구현중 == == 유닛 삭제 구현중 == == 유닛 삭제 구현중 == == 유닛 삭제 구현중 == == 유닛 삭제 구현중 == == 유닛 삭제 구현중 ==

    //    public void deleteAudioPerUnit(Long detailId, Long projectId) {
//        Project project = projectRepository.findById(projectId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));
//
//        if (project instanceof TTSProject) {
//            deleteTTSDetail(detailId);
//        } else if (project instanceof VCProject) {
//            deleteVCDetail(detailId);
//        } else if (project instanceof ConcatProject) {
//            deleteConcatDetail(detailId);
//        } else {
//            throw new BusinessException(ErrorCode.UNSUPPORTED_PROJECT_TYPE);
//        }
//    }
//
//    private void deleteTTSDetail(Long ttsDetailId) {
//        // 1. TTSDetail 조회
//        TTSDetail ttsDetail = ttsDetailRepository.findById(ttsDetailId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.DETAIL_NOT_FOUND));
//
//        // 2. 연결된 OutputAudioMeta 삭제
//        List<OutputAudioMeta> outputAudioMetas = outputAudioMetaRepository.findOutputAudioMetaByTTSDetailId(
//                ttsDetailId);
//        for (OutputAudioMeta outputAudioMeta : outputAudioMetas) {
//            deleteDirectoryFromS3(outputAudioMeta.getBucketRoute()); // S3 파일 삭제
//            outputAudioMeta.deleteOutputAudioMeta(); // isDeleted 및 삭제 시간 업데이트
//            outputAudioMetaRepository.save(outputAudioMeta);
//        }
//
//        // 3. TTSDetail 업데이트
//        ttsDetail.deleteTTSDetail(); // isDeleted 및 삭제 시간 업데이트
//        ttsDetailRepository.save(ttsDetail);
//    }
//
//    private void deleteVCDetail(Long vcDetailId) {
//        // 1. VCDetail 조회
//        VCDetail vcDetail = vcDetailRepository.findById(vcDetailId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.DETAIL_NOT_FOUND));
//
//        // 2. 연결된 OutputAudioMeta 삭제
//        List<OutputAudioMeta> outputAudioMetas = outputAudioMetaRepository.findOutputAudioMetaByVCDetailId(vcDetailId);
//        for (OutputAudioMeta outputAudioMeta : outputAudioMetas) {
//            deleteDirectoryFromS3(outputAudioMeta.getBucketRoute()); // S3 파일 삭제
//            outputAudioMeta.deleteOutputAudioMeta(); // isDeleted 및 삭제 시간 업데이트
//            outputAudioMetaRepository.save(outputAudioMeta);
//        }
//
//        // 3. VCDetail 업데이트
//        vcDetail.markAsDeleted(); // isDeleted 및 삭제 시간 업데이트
//        vcDetailRepository.save(vcDetail);
//    }
//
//    private void deleteConcatDetail(Long concatProjectId) {
//        // 1. ConcatProject 조회
//        ConcatProject concatProject = concatProjectRepository.findById(concatProjectId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.DETAIL_NOT_FOUND));
//
//        // 2. 연결된 OutputAudioMeta 삭제
//        List<OutputAudioMeta> outputAudioMetas = outputAudioMetaRepository.findOutputAudioMetaByConcatProjectId(
//                concatProjectId);
//        for (OutputAudioMeta outputAudioMeta : outputAudioMetas) {
//            deleteDirectoryFromS3(outputAudioMeta.getBucketRoute()); // S3 파일 삭제
//            outputAudioMeta.deleteOutputAudioMeta(); // isDeleted 및 삭제 시간 업데이트
//            outputAudioMetaRepository.save(outputAudioMeta);
//        }
//
//        // 3. ConcatProject 업데이트
//        concatProject.deletedAt(); // isDeleted 및 삭제 시간 업데이트
//        concatProjectRepository.save(concatProject);
//    }
//
//
    // Prefix + bucket 값만 있으면 S3에서 삭제 가능
    public void deleteDirectoryFromS3(String directoryPrefix) {
        try {
            // listObjects라는 메서드를 통해서 버킷에서 object리스트를 가지고 옴.
            ObjectListing objectListing = amazonS3.listObjects(bucket, directoryPrefix);

            while (true) {
                // 삭제할 키들을 저장 할 곳
                List<DeleteObjectsRequest.KeyVersion> keysToDelete = new ArrayList<>();

                // 객체의 키에 삭제할 ObjectList들의 정보들을 담는다.
                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                    keysToDelete.add(new DeleteObjectsRequest.KeyVersion(objectSummary.getKey()));
                    System.out.println("Deleting: " + objectSummary.getKey());
                }

                // 만약 삭제할 키가 있다면
                if (!keysToDelete.isEmpty()) {
                    DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucket)
                            .withKeys(keysToDelete)
                            .withQuiet(false); // 기본값 false  삭제된 각 객체에 대한 정보를 로그로 출력
                    amazonS3.deleteObjects(deleteRequest);// 객체를 삭제하는 요청을 실제로 실행하는 메서드
                    System.out.println("배치 삭제 완료");
                }

                if (objectListing.isTruncated()) {  // 목록을 처리하면서 더 많은 객체가 있는지 확인.true면 더 많은 객체 존재함. false면 마지막 목록이다라는 뜻
                    objectListing = amazonS3.listNextBatchOfObjects(objectListing);
                } else {
                    break;
                }
            }
            System.out.println("All objects under directory deleted: " + directoryPrefix);
        } catch (Exception e) {
            System.err.println("Failed to delete objects from S3: " + e.getMessage());
        }
    }

    // ========================================================================================================================

    /**
     * S3에서 파일을 다운로드하여 로컬에 저장합니다.
     *
     * @param fileUrl  다운로드할 파일의 S3 URL
     * @param localDir 로컬에 저장할 디렉토리 경로
     * @return 로컬에 저장된 파일의 전체 경로
     */
    public String downloadFileFromS3(String fileUrl, String localDir) {
        try {
            // S3 버킷 이름과 키를 추출
            URI uri = URI.create(fileUrl);
            String bucketName = bucket; // 버킷 이름은 설정된 값을 사용
            String key = uri.getPath().substring(1); // '/' 제거

            // 파일 이름 추출
            String fileName = Paths.get(key).getFileName().toString();

            // 로컬 저장 경로 설정
            Path localFilePath = Paths.get(localDir, fileName);
            Files.createDirectories(localFilePath.getParent()); // 디렉토리 생성

            // S3에서 파일 다운로드
            S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucketName, key));
            try (S3ObjectInputStream s3is = s3Object.getObjectContent();
                 FileOutputStream fos = new FileOutputStream(localFilePath.toFile())) {
                byte[] readBuf = new byte[1024];
                int readLen;
                while ((readLen = s3is.read(readBuf)) > 0) {
                    fos.write(readBuf, 0, readLen);
                }
            }

            return localFilePath.toString();
        } catch (AmazonClientException e) {
            throw new BusinessException(ErrorCode.S3_DOWNLOAD_FAILED);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }

    public List<MemberAudioMeta> uploadAndSaveMemberFile2(List<MultipartFile> files, Long memberId, Long projectId,
                                                          AudioType audioType) {
        try {
            List<MemberAudioMeta> memberAudioMetas = new ArrayList<>();
            // 필요한 리포지토리나 서비스를 주입받아 사용해야 합니다.
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    throw new BusinessException(ErrorCode.EMPTY_FILE);
                }

                String originFilename = Normalizer.normalize(file.getOriginalFilename(), Normalizer.Form.NFC);
                String filename = "member/" + memberId + "/" + audioType + "/" + projectId + "/" + originFilename;

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(file.getContentType());
                metadata.setContentLength(file.getSize());

                // S3 버킷에 파일 업로드
                amazonS3Client.putObject(bucket, filename, file.getInputStream(), metadata);
                String fileUrl = amazonS3Client.getUrl(bucket, filename).toString();

                // MemberAudioMeta 객체 생성 및 저장
                MemberAudioMeta memberAudioMeta = MemberAudioMeta.createMemberAudioMeta(member, filename, fileUrl,
                        audioType);
                memberAudioMetaRepository.save(memberAudioMeta);

                memberAudioMetas.add(memberAudioMeta);
            }

            return memberAudioMetas;

        } catch (AmazonClientException e) {
            // S3 업로드 중 발생하는 예외
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED);
        } catch (IOException e) {
            // 파일 처리 중 발생하는 예외
            throw new BusinessException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }
}
