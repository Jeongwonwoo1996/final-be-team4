package com.fourformance.tts_vc_web.service.common;

import com.fourformance.tts_vc_web.common.constant.TaskStatusConst;
import com.fourformance.tts_vc_web.common.exception.common.BusinessException;
import com.fourformance.tts_vc_web.common.exception.common.ErrorCode;
import com.fourformance.tts_vc_web.domain.entity.Member;
import com.fourformance.tts_vc_web.domain.entity.Project;
import com.fourformance.tts_vc_web.domain.entity.Task;
import com.fourformance.tts_vc_web.dto.common.TaskLoadDto;
import com.fourformance.tts_vc_web.dto.response.ResponseDto;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import com.fourformance.tts_vc_web.repository.ProjectRepository;
import com.fourformance.tts_vc_web.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;

    public TaskService(ProjectRepository projectRepository, TaskRepository taskRepository, MemberRepository memberRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public List<TaskLoadDto> getTasksByMemberAndConditions(Long memberId) {

        //Member member = MemberRepository.findById(memberId);
        if(memberId == null) { throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND); }

        List<Task> taskList = taskRepository.findTasksByMemberIdAndConditions(memberId);

        return taskList.stream()
                .map(TaskLoadDto::createTaskLoadDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void terminatePendingTasks(Long memberId) {

        // 1. 존재하는 회원 ID가 있는지 찾기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 회원 Id로 모든 프로젝트 조회
        List<Long> projectIdList = projectRepository.findByMemberId(member.getId());

        // 3. 프로젝트Id 로 completed, terminated가 아닌 모든 상태의 작업 조회
        List<Task> pendingTasks = taskRepository.findByStatus(projectIdList);

        // 4. 상태를 Terminated로 변경
        for (Task task : pendingTasks) {
            task.updateStatus(TaskStatusConst.TERMINATED);
            taskRepository.save(task);
        }

    }
}

