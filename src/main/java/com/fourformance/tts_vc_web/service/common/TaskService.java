package com.fourformance.tts_vc_web.service.common;

import com.fourformance.tts_vc_web.domain.entity.Task;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

//    public TaskLoadDto getTasksByMemberAndConditions(Long memberId) {
//        List<Task> taskList = taskRepository.findTasksByMemberIdAndConditions(memberId);
//
//
//        return null;
//    }
}

