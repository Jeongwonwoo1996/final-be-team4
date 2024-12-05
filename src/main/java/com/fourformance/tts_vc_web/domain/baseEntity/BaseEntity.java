package com.fourformance.tts_vc_web.domain.baseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(updatable = false)
//    private String createdBy = "imsi"; // 회원 담당하는 사람이 알아서 세션에서 회원 id 가져와서 수정자 생성장에 값 채워넣기
    private Long createdBy; // 필터에서 설정된 사용자 정보가 자동 채워짐

    @LastModifiedBy
//    public String lastModifiedBy = "imsi";
    private Long lastModifiedBy; // 필터에서 설정된 사용자 정보가 자동 채워짐
}
