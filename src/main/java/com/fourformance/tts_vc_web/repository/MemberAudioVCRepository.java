package com.fourformance.tts_vc_web.repository;

import com.fourformance.tts_vc_web.domain.entity.MemberAudioVC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberAudioVCRepository extends JpaRepository<MemberAudioVC, Long> {
}
