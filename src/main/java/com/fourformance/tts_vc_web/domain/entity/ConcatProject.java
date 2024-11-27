package com.fourformance.tts_vc_web.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "concat_project")
public class ConcatProject extends Project {
    private Float globalFrontSilenceLength = 0.0F;
    private Float globalTotalSilenceLength = 0.0F;
    private String projectType = "CONCAT";

    // 생성 메서드
    public static ConcatProject createConcatProject(Member member, String projectName) {
        ConcatProject concatProject = new ConcatProject();
        concatProject.member = member;
        concatProject.projectName = projectName;
        concatProject.createdAt();
        concatProject.updatedAt();
        return concatProject;
    }

    // 업데이트 메서드
    public void updateConcatProject(String projectName, Float globalFrontSilenceLength,
                                    Float globalTotalSilenceLength) {
        super.projectName = projectName;
        this.globalFrontSilenceLength = globalFrontSilenceLength;
        this.globalTotalSilenceLength = globalTotalSilenceLength;
        super.updatedAt();
    }

    // 삭제 메서드
    public void deleteConcatProject() {
        super.isDeleted = true;
        super.deletedAt();
    }

    // 복구 메서드
    public void restoreConcatProject() {
        this.isDeleted = false;
        super.deletedAtNull();
    }
}