package com.fourformance.tts_vc_web.controller.common;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.dto.common.TaskLoadDto;
import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.service.common.TaskProducer;
import com.fourformance.tts_vc_web.service.common.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskProducer taskProducer;
    private final TaskService taskService;

    @PostMapping
    public String sendTask(@RequestParam String taskType, @RequestBody String message) {
        taskProducer.sendTask(taskType, message);
        return "Task sent: " + taskType;
    }

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

        return DataResponseDto.of(taskLoadDtos, "작업 목록");
    }

    @Operation(
            summary = "작업 초기화",
            description = "지금까지 걸어놓은 모든 작업 현황을 삭제합니다." )
    @DeleteMapping("/clear")
    public ResponseDto clear(){
        return DataResponseDto.of("");
    }

    @Operation(
            summary = "실패 작업 재실행 버튼",
            description = "실패한 작업을 재실행합니다." )
    @PostMapping("/restart")
    public ResponseDto restart(){
        return DataResponseDto.of("");
    }

}
