package com.fourformance.tts_vc_web.repository;

import com.fourformance.tts_vc_web.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 작업 데이터(Json)으로 데이터 찾기 - 승민
    @Query(value = "SELECT t.id FROM Task t WHERE JSON_EXTRACT(task_data, '$.id') = :id", nativeQuery = true)
    Long findByNameInJson(@Param("id") Long id);

    @Query(value = """
    SELECT DISTINCT t.*
       FROM task t
       JOIN project p ON t.project_id = p.project_id
       WHERE p.member_id = 1
         AND (
             t.task_status_const IN ('WAITING', 'RUNNABLE', 'FAILED')
             OR (t.task_status_const = 'COMPLETED' AND t.last_modified_date >= NOW() - INTERVAL 1 DAY)
         );
""", nativeQuery = true)
    List<Task> findTasksByMemberIdAndConditions(@Param("memberId") Long memberId);

}
