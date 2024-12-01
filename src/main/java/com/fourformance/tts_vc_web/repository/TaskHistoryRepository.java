package com.fourformance.tts_vc_web.repository;

import com.fourformance.tts_vc_web.domain.entity.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {

    // 작업ID로 이전 작업 상태 찾기 - 승민
    @Query("SELECT th FROM TaskHistory th " +
            "WHERE th.task.id = :taskId " +
            "ORDER BY th.created_at DESC")
    TaskHistory findLatestTaskHistoryByTaskId(@Param("taskId") Long taskId);
}
