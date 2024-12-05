package com.fourformance.tts_vc_web.controller.common;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.dto.common.TaskLoadDto;
import com.fourformance.tts_vc_web.dto.concat.ConcatRequestDto;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.dto.tts.TTSRequestDto;
import com.fourformance.tts_vc_web.dto.vc.VCSaveRequestDto;
import com.fourformance.tts_vc_web.service.common.TaskProducer;
import com.fourformance.tts_vc_web.service.common.TaskService;
import com.fourformance.tts_vc_web.service.concat.ConcatService_TaskJob;
import com.fourformance.tts_vc_web.service.tts.TTSService_TaskJob;
import com.fourformance.tts_vc_web.service.vc.VCService_TaskJob;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TTSService_TaskJob ttsServiceTaskJob;
    private final VCService_TaskJob vcServiceTask;
    private final ConcatService_TaskJob concatTaskService; // 병합 서비스 의존성 주입
    private final TaskService taskService;

    @Operation(
            summary = "작업 가져오기",
            description = "Save 버튼을 이용하여 백업해놓은 작업을 불러와서 실행합니다." )
    @GetMapping("/load")
    public ResponseDto load(HttpSession session){

        // memberId 세션에서 가져오기
        if (session.getAttribute("memberId") == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Long memberId = (Long) session.getAttribute("memberId");

        List<TaskLoadDto> taskLoadDtos = taskService.getTasksByMemberAndConditions(memberId);

        return DataResponseDto.of(taskLoadDtos, "작업 목록 로드 성공");
    }

    @Operation(
            summary = "작업 초기화",
            description = "지금까지 걸어놓은 모든 작업 현황을 삭제합니다." )
    @DeleteMapping("/clear")
    public ResponseDto clear(HttpSession session){

        // memberId 세션에서 가져오기
        if (session.getAttribute("memberId") == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Long memberId = (Long) session.getAttribute("memberId");

        taskService.terminatePendingTasks(memberId);

        return DataResponseDto.of("","작업 초기화 성공");
    }

    @Operation(
            summary = "실패 작업 재실행 버튼",
            description = "실패한 작업을 재실행합니다." )
    @GetMapping("/restart")
    public ResponseDto restart(){
        int restartedCount = taskService.restartFailedTasks();
        return DataResponseDto.of("총 " + restartedCount + "개의 실패 작업이 재실행되었습니다.");
    }


    @PostMapping("/convert/tts")
    public ResponseDto convertBatchTexts(
            @RequestBody TTSRequestDto ttsRequestDto,
            HttpSession session) {

        Long memberId = (Long) session.getAttribute("memberId");

        // 세션에 memberId 값이 설정되지 않았다면 예외 처리
        if (memberId == null) {
            throw new BusinessException(ErrorCode.SESSION_MEMBER_ID_NOT_SET);
        }

        ttsServiceTaskJob.enqueueTTSBatchTasks(ttsRequestDto, memberId);

        return DataResponseDto.of("TTS 작업이 큐에 추가되었습니다.");

    }


    @PostMapping(value = "/convert/vc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto processVCProject(
            @RequestPart("VCSaveRequestDto") VCSaveRequestDto vcSaveRequestDto,
            @RequestPart("files") List<MultipartFile> files,
            HttpSession session) {

        // 세션에 memberId 값이 설정되지 않았다면 예외 처리
        if (session.getAttribute("memberId") == null) {
            throw new BusinessException(ErrorCode.SESSION_MEMBER_ID_NOT_SET);
        }

        // 세션에서 memberId 가져오기
        Long memberId = (Long) session.getAttribute("memberId");

        // 요청 데이터 유효성 검사
        validateRequestData(vcSaveRequestDto);

        vcServiceTask.enqueueVCTasks(vcSaveRequestDto, files, memberId);

        return DataResponseDto.of("vc 작업이 큐에 추가되었습니다.");

    }

    @PostMapping(
            value = "/convert/concat",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseDto convertMultipleAudios(
            @RequestPart("concatRequestDto") @Parameter(description = "요청 DTO") ConcatRequestDto concatRequestDto,
            @RequestPart("files") @Parameter(description = "업로드할 파일들") List<MultipartFile> files, HttpSession session
    ) {
        // 세션에 memberId 값이 설정되지 않았다면 예외 처리
        if (session.getAttribute("memberId") == null) {
            throw new BusinessException(ErrorCode.SESSION_MEMBER_ID_NOT_SET);
        }

        // 세션에 memberId 값 설정
        Long memberId = (Long) session.getAttribute("memberId");


        concatTaskService.enqueueConcatTask(concatRequestDto,files,memberId);


        return DataResponseDto.of("Concat 작업이 큐에 추가되었습니다.");


    }

    /**
     * 요청 데이터 유효성 검사
     *
     * @param vcSaveRequestDto 요청 데이터
     * @throws BusinessException 잘못된 요청 데이터인 경우 예외 발생
     */
    private void validateRequestData(VCSaveRequestDto vcSaveRequestDto) {
        if (vcSaveRequestDto == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA);
        }
        if (vcSaveRequestDto.getTrgFiles() == null || vcSaveRequestDto.getTrgFiles().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_FILE_DATA);
        }
    }

}
