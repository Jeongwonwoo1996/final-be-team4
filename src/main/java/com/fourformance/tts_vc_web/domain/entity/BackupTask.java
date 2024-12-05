package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.common.constant.BackupType;
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
@Table(name = "backup_task")
public class BackupTask extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "backup_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(columnDefinition = "JSON")
    private String backupData;

    @Enumerated(EnumType.STRING)
    private BackupType backupType;

    private LocalDateTime created_at;

    private static BackupTask createBackupTask(Task task, String backupData, BackupType backupType){
        BackupTask backupTask = new BackupTask();
        backupTask.task       = task;
        backupTask.backupData = backupData;
        backupTask.backupType = backupType;
        backupTask.created_at = LocalDateTime.now();
        return backupTask;
    }
}
