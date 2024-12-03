package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.common.constant.AudioFormat;
import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.domain.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAudioMeta extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_audio_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String bucketRoute;

    @Lob
    private String audioUrl;
    private String script;
    @Enumerated(EnumType.STRING)
    private AudioType audioType;

    @Column(name = "trg_voice_id", length = 50)
    private String trgVoiceId;

    private Boolean isDeleted = false;
    private Boolean isSelected = false;

    @Enumerated(EnumType.STRING)
    private AudioFormat audioFormat = AudioFormat.WAV;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;


    // 생성 메서드
    public static MemberAudioMeta createMemberAudioMeta(Member member, String bucketRoute, String audioUrl,
                                                        AudioType audioType) {
        MemberAudioMeta memberAudioMeta = new MemberAudioMeta();
        memberAudioMeta.bucketRoute = bucketRoute;
        memberAudioMeta.member = member;
        memberAudioMeta.audioUrl = audioUrl;
        memberAudioMeta.audioType = audioType;
        memberAudioMeta.createdAt = LocalDateTime.now();
        return memberAudioMeta;
    }

    // trg voiceId 주입 메서드
    public void update(String trgVoiceId) {
        this.trgVoiceId = trgVoiceId;
    }


    // 삭제 메서드
    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

}