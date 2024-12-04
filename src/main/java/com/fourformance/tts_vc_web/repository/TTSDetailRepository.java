package com.fourformance.tts_vc_web.repository;

import com.fourformance.tts_vc_web.domain.entity.TTSDetail;
import com.fourformance.tts_vc_web.domain.entity.VoiceStyle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TTSDetailRepository extends JpaRepository<TTSDetail, Long> {

    // 프로젝트 ID로 TTS 상세 값들을 찾아 리스트로 반환 - 승민
    List<TTSDetail> findByTtsProject_Id(Long projectId);

    // TTS Detail Id가 담긴 List로 TTSDetail 객체 반환 받기 - 승민
    @Query("SELECT t FROM TTSDetail t WHERE t.id IN :ttsDetailIdList")
    List<TTSDetail> findByTtsDetailIds(@Param("ttsDetailIdList") List<Long> ttsDetailIdList);


    /**
     * ID로 VoiceStyle 엔티티를 조회합니다.
     *
     * @param id 조회할 VoiceStyle의 ID
     * @return 해당 ID에 해당하는 VoiceStyle 엔티티 작성자: 정원우
     */
    @Query("SELECT vs FROM VoiceStyle vs WHERE vs.id = :id")
    VoiceStyle findVoiceStyleById(@Param("id") Long id);

    /**
     * 특정 TTS 프로젝트에 속한 TTSDetail ID 목록을 조회합니다.
     *
     * @param projectId 조회할 TTS 프로젝트 ID
     * @return 해당 프로젝트에 속한 TTSDetail ID 목록 작성자: 정원우
     */
    @Query("SELECT d.id FROM TTSDetail d WHERE d.ttsProject.id = :projectId")
    List<Long> findDetailIdsByProjectId(@Param("projectId") Long projectId);


}
