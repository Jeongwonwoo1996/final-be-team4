package com.fourformance.tts_vc_web.dto.concat;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ConcatMsgDetailDto {

    private Long detailId;
    private Integer audioSeq;
    private String unitScript;
    private Float endSilence;
    private String srcUrl;
}
