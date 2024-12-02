package com.fourformance.tts_vc_web.repository;

import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.domain.entity.MemberAudioMeta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberAudioMetaRepository extends JpaRepository<MemberAudioMeta, Long> {

    // 모든 오디오 isSelected = false로 설정 - 승민
    @Modifying
    @Query("UPDATE MemberAudioMeta m SET m.isSelected = false WHERE m.audioType = :audioType")
    void resetSelection(@Param("audioType") AudioType audioType);

    // 특정 audio만 isSelected = true로 설정 - 승민
    @Modifying
    @Query("UPDATE MemberAudioMeta m SET m.isSelected = true WHERE m.id = :audioId AND m.audioType = :audioType")
    void selectAudio(@Param("audioId") Long audioId, @Param("audioType") AudioType audioType);


    // audioType, memberId와 isSeleted=true 조건으로 memberAudioId 반환 - 승민
    @Query("SELECT m FROM MemberAudioMeta m WHERE m.audioType = :audioType AND m.isSelected = true AND m.member.id = :memberId")
    MemberAudioMeta findSelectedAudioByTypeAndMember(@Param("audioType") AudioType audioType, @Param("memberId") Long memberId);


    // id 리스트로 특정 오디오 타입 반환 - 승민
    @Query("""
                SELECT m 
                FROM MemberAudioMeta m 
                WHERE m.id IN :memberAudioIds 
                  AND m.isDeleted = false 
                  AND m.audioType = :audioType
            """)
    List<MemberAudioMeta> findByMemberAudioIds(
            @Param("memberAudioIds") List<Long> memberAudioIds,
            @Param("audioType") AudioType audioType
    );


    // 특정 id와 audio type 으로 객체 반환 - 승민
    @Query("SELECT m FROM MemberAudioMeta m WHERE m.id = :id AND m.audioType = :audioType")
    MemberAudioMeta findByIdAndAudioType(
            @Param("id") Long id,
            @Param("audioType") AudioType audioType);

    // 특정 사용자의 특정 AudioType을 가진 MemberAudioMeta를 조회 - 재홍
    List<MemberAudioMeta> findByMemberIdAndAudioType(Long memberId, AudioType audioType);

    // VC TRG 오디오 url 추출 - 승민
    @Query("""
                SELECT m.audioUrl 
                FROM MemberAudioMeta m 
                WHERE m.id = :audioMetaId 
                  AND m.isDeleted = false 
                  AND m.audioType = :audioType 
                  AND m.audioUrl IS NOT NULL
            """)
    String findAudioUrlsByAudioMetaIds(
            @Param("audioMetaId") Long audioMetaId,
            @Param("audioType") AudioType audioType
    );

    /**
     * S3에 업로드된 오디오 URL을 통해 MemberAudioMeta를 조회하는 메서드
     *
     * @param audioUrl 업로드된 오디오 파일의 URL
     * @return MemberAudioMeta 엔티티 (없으면 Optional.empty())
     */
    Optional<MemberAudioMeta> findFirstByAudioUrl(String audioUrl);

    // Concat Detail Id로 업로드된 오디오들을 찾아 리스트로 반환 - 의준
    @Query("SELECT m " +
            "FROM ConcatDetail c " +
            "JOIN c.memberAudioMeta m " +
            "WHERE c.id IN :concatDetailIds ")
    List<MemberAudioMeta> findByConcatDetailIds(@Param("concatDetailIds") List<Long> concatDetailIds);


    // (VC) project id로 유저오디오메타 찾기 (타겟)  - 의준, 소정
    @Query("SELECT ma.id FROM MemberAudioMeta ma " +
            "JOIN VCProject p ON ma.id = p.memberTargetAudioMeta.id " +
            "WHERE p.id = :projectId")
    Long findTargetAudioMetaIdByVCProjectId(@Param("projectId") Long projectId);

    // (VC) project id로 유저오디오메타 찾기 (소스) - 의준, 소정
    @Query("SELECT d.memberAudioMeta.id FROM VCDetail d " +
            "WHERE d.vcProject.id = :vcProjectId")
    List<Long> findSourceAudioMetaIdsByVCProjectId(@Param("vcProjectId") Long vcProjectId);

    // (Concat) project id로 유저오디오메타 찾기 (컨캣 소스) - 의준, 소정
    @Query("SELECT cd.memberAudioMeta.id FROM ConcatDetail cd " +
            "WHERE cd.concatProject.id = :concatProjectId")
    List<Long> findMemberAudioMetaIdsByConcatProjectId(@Param("concatProjectId") Long concatProjectId);

}
