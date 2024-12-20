package com.fourformance.tts_vc_web.repository;

import com.fourformance.tts_vc_web.domain.entity.VCDetail;
import com.fourformance.tts_vc_web.domain.entity.VCProject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VCDetailRepository extends JpaRepository<VCDetail, Long> {

    // 프로젝트 ID로 VC 상세 값들을 찾아 리스트로 반환 - 승민
    List<VCDetail> findByVcProject_Id(Long projectId);

    // VC Detail Id가 담긴 List로 VCDetail 객체 반환 받기 - 승민
    @Query("SELECT t FROM VCDetail t WHERE t.id IN :vcDetailIdList")
    List<VCDetail> findByVcDetailIds(@Param("vcDetailIdList") List<Long> vcDetailIdList);

    // VC Datail Id 리스트로 memberAudioMetaID 리스트로 반환 - 승민
    @Query("""
                SELECT v.memberAudioMeta.id 
                FROM VCDetail v 
                WHERE v.id IN :vcDetailIds 
                  AND v.isDeleted = true
            """)
    List<Long> findMemberAudioIdsByVcDetailIds(@Param("vcDetailIds") List<Long> vcDetailIds);

    // VCProject와 연관된 VCDetail 목록 조회
    List<VCDetail> findByVcProject(VCProject vcProject);

    // vc projec id로 모든 vc detail 추출 - 의준
    @Query("SELECT v FROM VCDetail v WHERE v.vcProject.id = :projectId")
    List<VCDetail> findAllByProjectId(@Param("projectId") Long projectId);

}
