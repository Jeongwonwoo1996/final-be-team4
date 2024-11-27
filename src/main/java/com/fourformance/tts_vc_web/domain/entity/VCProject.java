package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.common.constant.APIStatusConst;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "vc_project")
public class VCProject extends Project {

    @Enumerated(EnumType.STRING)
    private APIStatusConst apiStatus = APIStatusConst.NOT_STARTED; // enum 생성 필요

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_audio_id")
    private MemberAudioMeta memberTargetAudioMeta;

    private LocalDateTime APIStatusModifiedAt;

    @Column(name = "trg_voice_id", length = 50)
    private String trgVoiceId; // 새로 추가된 필드 -> 타겟 보이스 Id

    private String projectType = "VC";

    // 생성 메서드
    public static VCProject createVCProject(Member member, String projectName) {
        VCProject vcProject = new VCProject();
        vcProject.member = member;
        vcProject.projectName = projectName;
        vcProject.trgVoiceId = null; // 초기 값 설정
        vcProject.createdAt();
        vcProject.updatedAt();
        return vcProject;
    }

    // 업데이트 메서드
    public void updateVCProject(String projectName, MemberAudioMeta memberTargetAudioMeta) {
        this.memberTargetAudioMeta = memberTargetAudioMeta;
        super.projectName = projectName;
        super.updatedAt();
    }

    // 타겟 오디오 메타 주입 메서드
    public void injectTargetAudioMeta(MemberAudioMeta memberTargetAudioMeta) {
        this.memberTargetAudioMeta = memberTargetAudioMeta;
    }

    // Voice ID 업데이트 메서드 (새롭게 분리)
    // 타겟 Voice ID 업데이트 메서드
    public void updateTrgVoiceId(String trgVoiceId) {
        this.trgVoiceId = trgVoiceId;
    }

    // API 상태 변경 메서드
    public void updateAPIStatus(APIStatusConst apiStatus) {
        this.apiStatus = apiStatus;
        this.APIStatusModifiedAt = LocalDateTime.now();
    }

    // 삭제 메서드
    public void deleteVCProject() {
        super.isDeleted = true;
        super.deletedAt();
    }

    // 복구 메서드
    public void restoreVCProject() {
        super.isDeleted = false;
        super.deletedAtNull();
    }


}
