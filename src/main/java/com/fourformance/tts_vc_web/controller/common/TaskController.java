package com.fourformance.tts_vc_web.controller.common;

import com.fourformance.tts_vc_web.dto.response.DataResponseDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.service.common.TaskProducer;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskProducer taskProducer;

    @PostMapping
    public String sendTask(@RequestParam String taskType, @RequestBody String message) {
        taskProducer.sendTask(taskType, message);
        return "Task sent: " + taskType;
    }

    @Operation(
            summary = "작업 가져오기",
            description = "Save 버튼을 이용하여 백업해놓은 작업을 불러와서 실행합니다." )
    @GetMapping("/load")
    public ResponseDto load(){

        // 작업 로드

        return DataResponseDto.of("");
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
