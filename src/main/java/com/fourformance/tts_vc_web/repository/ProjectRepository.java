package com.fourformance.tts_vc_web.repository;

import com.fourformance.tts_vc_web.domain.entity.Project;
import com.fourformance.tts_vc_web.repository.workspace.ProjectRepositoryCustom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {
//public interface ProjectRepository extends JpaRepository<Project, Long> {

    //    @Query(value = "SELECT p " +
//            "FROM Project p " +
//            "WHERE p.member.id = :memberId " +
//            "ORDER BY p.createdAt DESC")
    List<Project> findTop5ByMemberIdOrderByUpdatedAtDesc(@Param("memberId") Long memberId);

    // 멤버 아이디가 같고 project의 컬럼이 isDeleted인지 확인.
    @Query("""
            SELECT p
            FROM Project p
            WHERE p.member.id = :memberId AND p.isDeleted = false
            ORDER BY p.createdAt DESC LIMIT 5
            """)
    List<Project> findTop5ByMemberIdOrderByCreatedAtDesc(@Param("memberId") Long memberId);

}