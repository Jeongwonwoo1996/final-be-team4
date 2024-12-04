package com.fourformance.tts_vc_web.domain.entity;

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
@Table(name = "task_history")
public class TaskHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Enumerated(EnumType.STRING)
    private TaskStatusConst oldStatus;
    @Enumerated(EnumType.STRING)
    private TaskStatusConst newStatus;
    private String mod_msg;
    private LocalDateTime created_at;

    public static TaskHistory createTaskHistory(Task task, TaskStatusConst oldStatus, TaskStatusConst newStatus, String mod_msg){
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.task       = task;
        taskHistory.oldStatus  = oldStatus;
        taskHistory.newStatus  = newStatus;
        taskHistory.mod_msg    = mod_msg;
        taskHistory.created_at = LocalDateTime.now();

        return taskHistory;
    }

}
