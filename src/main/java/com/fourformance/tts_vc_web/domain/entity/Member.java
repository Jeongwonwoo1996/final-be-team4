package com.fourformance.tts_vc_web.domain.entity;

import com.fourformance.tts_vc_web.domain.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String pwd;
    private String name;
    private Integer gender;
    private LocalDateTime birthDate;
    private String phoneNumber;
    private String tou;
    private Boolean is_deleted = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // 생성 메서드
    public static Member createMember(String email, String pwd, String name,
                                      Integer gender, LocalDateTime birthDate,
                                      String phoneNumber) {
        Member member = new Member();
        member.email = email;
        member.pwd = pwd;
        member.name = name;
        member.gender = gender;
        member.birthDate = birthDate;
        member.phoneNumber = phoneNumber;
        member.createdAt = LocalDateTime.now();
        member.updatedAt = LocalDateTime.now();
        return member;
    }

    // 업데이트 메서드
    public void updateMember(String pwd, String phoneNumber, LocalDateTime birthDate) {
        this.pwd = pwd;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.updatedAt = LocalDateTime.now();
    }

    // 삭제 메서드
    public void deleteMember() {
        this.is_deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    // 복구 메서드
    public void restoreMember() {
        this.is_deleted = false;
        this.deletedAt = null;
    }

    // 회원 이름 업데이트 메서드 추가 - 원우
    public void updateMemberName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    // 회원 전화번호 업데이트 메서드 추가 - 원우
    public void updateMemberPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }

    // 회원 비밀번호 업데이트 메서드 추가 - 원우
    public void updateMemberPwd(String pwd) {
        this.pwd = pwd;
        this.updatedAt = LocalDateTime.now();
    }





}