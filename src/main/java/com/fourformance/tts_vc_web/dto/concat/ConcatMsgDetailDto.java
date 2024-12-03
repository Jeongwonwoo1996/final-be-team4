package com.fourformance.tts_vc_web.dto.concat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConcatMsgDetailDto {

    private Long detailId;
    private Integer audioSeq;
    private String unitScript;
    private Float endSilence;
    private String srcUrl;
}
