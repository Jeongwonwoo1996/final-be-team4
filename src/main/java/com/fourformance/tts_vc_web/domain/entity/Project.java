package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.domain.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn
public abstract class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    protected Member member;


    protected String projectName;
    protected Boolean isDeleted = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // projectName 업데이트 메서드
    public void updateProjectName(String newProjectName) {
        this.projectName = newProjectName;
    }

    // createdAt 업데이트 메서드
    public void createdAt() {
        this.createdAt = LocalDateTime.now();
    }

    // updatedAt 업데이트 메서드
    public void updatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    // deletedAt 업데이트 메서드
    public void deletedAt() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    // deletedAt 초기화 메서드
    public void deletedAtNull() {
        this.deletedAt = null;
    }

}