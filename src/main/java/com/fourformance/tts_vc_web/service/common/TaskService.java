package com.fourformance.tts_vc_web.service.common;

import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.Member;
import com.fourformance.tts_vc_web.domain.entity.Task;
import com.fourformance.tts_vc_web.dto.common.TaskLoadDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import com.fourformance.tts_vc_web.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskLoadDto> getTasksByMemberAndConditions(Long memberId) {

        //Member member = MemberRepository.findById(memberId);
        if(memberId == null) { throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND); }

        List<Task> taskList = taskRepository.findTasksByMemberIdAndConditions(memberId);

        return taskList.stream()
                .map(TaskLoadDto::createTaskLoadDto)
                .collect(Collectors.toList());
    }
}

