package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.common.constant.ProjectType;
import com.fourformance.tts_vc_web.common.constant.TaskStatusConst;
import com.fourformance.tts_vc_web.domain.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "task")
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    private ProjectType projectType;

    @Enumerated(EnumType.STRING)
    private TaskStatusConst taskStatusConst = TaskStatusConst.NEW;

    @Column(columnDefinition = "JSON")
    private String taskData; //작업(Task)을 실행하는 데 필요한 구체적인 데이터
    private String trackingId;
    private String resultMsg;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public static Task createTask( Project project, ProjectType projectType, String taskData) {
        Task task = new Task();
        task.taskData        = taskData;
        task.project         = project;
        task.projectType     = projectType;
        task.createdAt       = LocalDateTime.now();
        return task;
    }

    public void updateTrackingId( String trackingId) {
        this.trackingId      = trackingId;
        this.updatedAt       = LocalDateTime.now();
    }

    public void updateStatus(TaskStatusConst taskStatusConst) {
        this.taskStatusConst = taskStatusConst;
        this.updatedAt       = LocalDateTime.now();
    }

}
