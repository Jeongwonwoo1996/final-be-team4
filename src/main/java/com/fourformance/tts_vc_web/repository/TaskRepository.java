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
    SELECT t.*
    FROM Task t
    JOIN Project p ON t.project_id = p.id
    WHERE p.member_id = :memberId
      AND (
          EXISTS (
              SELECT 1
              FROM Task t2
              WHERE t2.project_id = p.id
                AND t2.status IN ('PENDING', 'IN_PROGRESS', 'FAILED')
          )
          OR
          (t.status = 'COMPLETED' AND t.status_change_time >= NOW() - INTERVAL 1 DAY)
      )
""", nativeQuery = true)
    List<Task> findTasksByMemberIdAndConditions(@Param("memberId") Long memberId);

}
