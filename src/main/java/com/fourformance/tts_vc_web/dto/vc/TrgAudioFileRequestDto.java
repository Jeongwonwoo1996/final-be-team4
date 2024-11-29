package com.fourformance.tts_vc_web.dto.vc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrgAudioFileRequestDto extends AudioFileDto {
    private Long s3MemberAudioMetaId; //  S3 메타 정보 (DB에서 조회)
}