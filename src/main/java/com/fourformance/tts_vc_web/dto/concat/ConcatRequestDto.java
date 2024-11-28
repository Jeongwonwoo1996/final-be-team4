package com.fourformance.tts_vc_web.dto.concat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConcatRequestDto {

    private Long projectId;
//    private Long memberId;
    private String projectName;
    private Float globalFrontSilenceLength;
    private Float globalTotalSilenceLength;
    private List<ConcatRequestDetailDto> concatRequestDetails;
}
