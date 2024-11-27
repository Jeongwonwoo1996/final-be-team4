package com.fourformance.tts_vc_web.controller.common;

import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.service.common.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3_test")
public class S3Controller {

    private final S3Service s3Service;

    // TTS나 VC로 반환한 유닛 오디오를 업로드하는 api
    @Operation(summary = "유닛(TTS or VC) 오디오 업로드", description = "유닛 오디오를 S3 버킷에 저장하고 메타데이터를 DB에 저장하는 api입니다."
            + "<br><br>매개변수 : <br>- 유닛 id, <br>- 프로젝트 id, <br>- 프로젝트 타입 (TTS, VC, Concat), <br>- 오디오 파일")
    @PostMapping(value = {"/tts/upload-generated-audio-to-bucket",
            "/vc/upload-generated-audio-to-bucket"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto uploadUnit(@RequestParam("file") MultipartFile file,
                                  @RequestParam("detailId") Long detailId,
                                  @RequestParam("projectId") Long projectId, HttpSession session) {
        Long userId = 0L; // 실제 프로젝트에서는 세션을 사용하여 사용자 ID를 가져옵니다.
        String fileUrl = s3Service.uploadUnitSaveFile(file, userId, projectId, detailId);
        return DataResponseDto.of(fileUrl, "파일이 성공적으로 업로드되었습니다.");
    }

//    @Operation(
//            summary = "유닛(TTS or VC) 오디오 업로드",
//            description = "유닛 오디오를 S3 버킷에 저장하고 메타데이터를 DB에 저장하는 API입니다.<br>" +
//                    "<br>매개변수 : " +
//                    "<br>- 유닛 id (detailId)" +
//                    "<br>- 프로젝트 id (projectId)" +
//                    "<br>- 오디오 파일"
//    )
//    @PostMapping(value = {"/tts/upload-generated-audio-to-bucket",
//            "/vc/upload-generated-audio-to-bucket"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseDto uploadUnit(
//            @RequestParam("file") MultipartFile file,
//
//            @Schema(description = "유닛 ID (TTS 개별 유닛 객체에 대한 id를 의미하며)", example = "123")
//            @RequestParam("detailId") Long detailId,
//
//            @Schema(description = "프로젝트 ID", example = "456")
//            @RequestParam("projectId") Long projectId,
//
//            HttpSession session
//    ) {
//        Long userId = 0L; // 실제 프로젝트에서는 세션을 사용하여 사용자 ID를 가져옵니다.
//        String fileUrl = s3Service.uploadUnitSaveFile(file, userId, projectId, detailId);
//        return DataResponseDto.of(fileUrl, "파일이 성공적으로 업로드되었습니다.");
//    }

    // Concat으로 반환한 오디오를 업로드하는 api
    @Operation(summary = "Concat 오디오 업로드", description = "컨캣 오디오를 S3 버킷에 저장하고 메타데이터를 DB에 저장하는 api입니다."
            + "<br><br>매개변수 : <br>- 프로젝트 id, <br>- 오디오 파일")
    @PostMapping(value = "/concat/upload-generated-audio-to-bucket", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto uploadConcat(@RequestParam("file") MultipartFile file,
                                    @RequestParam("projectId") Long projectId, HttpSession session) {
        Long userId = 0L; // 실제 프로젝트에서는 세션을 사용하여 사용자 ID를 가져옵니다.
        String fileUrl = s3Service.uploadConcatSaveFile(file, userId, projectId);
        return DataResponseDto.of(fileUrl, "파일 업로드가 성공적으로 완료되었습니다.");
    }

    // 생성된 오디오를 다운받을 수 있는 presigned url을 제공하는 api
    @Operation(summary = "버킷에 있는 오디오 다운로드", description =
            "오디오를 S3 버킷으로부터 다운로드 받을수 있는 URL을 제공하는 API 입니다."
                    + "<br><br>매개변수:<br>- 버킷 경로")
    @GetMapping(value = {"/tts/download-generated-audio-from-bucket", "/vc/download-generated-audio-from-bucket",
            "/concat/download-generated-audio-from-bucket", "/vc/download-to-generate-audio-from-bucket",
            "/concat/download-to-generate-audio-from-bucket"})
    public ResponseDto downloadGeneratedAudio(@RequestParam("bucketRoute") String bucketRoute) {
        String presignedUrl = s3Service.generatePresignedUrl(bucketRoute);
        return DataResponseDto.of(presignedUrl, "파일 다운로드 URL 생성 성공");
    }

    @Operation(summary = "유저가 가지고 있는 오디오를 버킷에 저장", description =
            "VC, CONCAT으로 변환할 오디오를 클라이언트 로컬컴퓨터로부터 버킷에 저장하는 api입니다."
                    + "<br><br>매개변수:<br>- 파일, <br>- 멤버Id, <br>- projectId, <br>- audioType"
                    + "<br>오디오 타입이 VC_TRG일 경우 마지막 매개변수로 voiceId를 입력합니다.")
    @PostMapping(value = {"/vc/upload-local-to-bucket",
            "/concat/upload-local-to-bucket"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto uploadFiles(
            @RequestParam("files") List<MultipartFile> files, @RequestParam("memberId") Long memberId,
            @RequestParam("projectId") Long projectId, @RequestParam("audioType") String audioType) {
        AudioType enumAudioType = AudioType.valueOf(audioType);
        List<String> uploadedUrls = s3Service.uploadAndSaveMemberFile(files, memberId, projectId, enumAudioType);
        return DataResponseDto.of(uploadedUrls);
    }

//    @DeleteMapping("/delete")
//    public ResponseEntity<String> deleteOutputAudioMetaByS3(
//            @RequestParam("projectId") Long projectId
//    ) {
//        s3Service.deleteAudioPerProject(projectId);
//        return ResponseEntity.ok("삭제 성공하였습니다.");
//    }

    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<String> deleteOutputAudioMetaByS3(
            @PathVariable("projectId") Long projectId
    ) {
        System.out.println("projectId = " + projectId);
        s3Service.deleteAudioPerProject(projectId);
        return ResponseEntity.ok("삭제 성공하였습니다.");
    }

//    @DeleteMapping("/deleteMemberAudioMeta")
//    public ResponseEntity<String> deleteMemberAudio(@RequestParam Long meberId, @RequestParam Long projectId) {
//        s3Service.deleteMemberAudio(meberId, projectId);
//        return ResponseEntity.ok("삭제성공");
//    }
}