package com.fourformance.tts_vc_web.dto.vc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.domain.entity.MemberAudioMeta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SrcAudioFileRequestDto extends AudioFileDto {
    private Long id; //VC 디테일 id
    private AudioType audioType; // 오디오 타입 (VC_TRG 또는 VC_SRC)
    private String localFileName; // 로컬 업로드 파일의 이름 (MultipartFile 매칭)
    private String unitScript; // 선택 사항: 텍스트 스크립트 (nullable)
    private Boolean isChecked; // 체크 표시 여부
    @JsonIgnore
    private Long memberAudioMetaId;  //S3 파일이 업로드된 memberAudioMeta ID
    @JsonIgnore
    private MultipartFile sourceAudio;   // MultipartFile 필드 추가

}
