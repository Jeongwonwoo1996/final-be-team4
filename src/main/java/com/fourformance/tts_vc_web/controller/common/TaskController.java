package com.fourformance.tts_vc_web.controller.common;

import com.fourformance.tts_vc_web.service.common.TaskProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskProducer taskProducer;

    public TaskController(TaskProducer taskProducer) {
        this.taskProducer = taskProducer;
    }

    @PostMapping
    public String sendTask(@RequestParam String taskType, @RequestBody String message) {
        taskProducer.sendTask(taskType, message);
        return "Task sent: " + taskType;
    }

}
