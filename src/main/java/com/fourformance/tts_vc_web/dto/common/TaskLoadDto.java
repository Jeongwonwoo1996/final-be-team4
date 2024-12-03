package com.fourformance.tts_vc_web.dto.common;

import com.fourformance.tts_vc_web.common.constant.ProjectType;
import com.fourformance.tts_vc_web.common.constant.TaskStatusConst;
import com.fourformance.tts_vc_web.domain.entity.TTSProject;
import com.fourformance.tts_vc_web.domain.entity.Task;
import com.fourformance.tts_vc_web.dto.tts.TTSProjectDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

@Data
@Builder
@AllArgsConstructor
public class TaskLoadDto {
    private Long id;
    private Long projectId;
    private ProjectType projectType;
    private TaskStatusConst taskStatus;
    private String taskData;
    private String resultMsg;

    private static ModelMapper modelMapper = new ModelMapper();

    public Task createTask(){
        modelMapper.getConfiguration()
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true);
        return modelMapper.map(this, Task.class);
    }

    public static TaskLoadDto createTaskLoadDto(Task task) {
        return modelMapper.map(task, TaskLoadDto.class);
    }
}
