package com.fourformance.tts_vc_web.dto.common;

import com.fourformance.tts_vc_web.dto.concat.ConcatMsgDetailDto;
import com.fourformance.tts_vc_web.dto.concat.ConcatRequestDetailDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ConcatMsgDto {
    private Long memberId;          // 회원 ID
    private Long taskId;            // 작업을 추적하고 상태를 관리하기 위함
    private Long projectId;         // 프로젝트 ID
    private Float globalFrontSilenceLength;
    private List<ConcatMsgDetailDto> concatMsgDetailDtos;
}