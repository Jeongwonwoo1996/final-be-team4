package com.fourformance.tts_vc_web.repository;

import com.fourformance.tts_vc_web.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 작업 데이터(Json)으로 데이터 찾기 - 승민
    @Query(value = "SELECT t.id FROM Task t WHERE JSON_EXTRACT(task_data, '$.id') = :id", nativeQuery = true)
    Long findByNameInJson(@Param("id") Long id);
}
