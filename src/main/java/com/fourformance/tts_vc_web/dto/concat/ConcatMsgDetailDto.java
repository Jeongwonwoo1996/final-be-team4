package com.fourformance.tts_vc_web.dto.concat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConcatMsgDetailDto {

    private Long detailId;
    private Integer audioSeq;
    private String unitScript;
    private Float endSilence;
    private String srcUrl;
}
