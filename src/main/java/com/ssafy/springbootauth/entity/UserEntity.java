package com.ssafy.springbootauth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users") // 매핑될 테이블 이름
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 생성 (MySQL AUTO_INCREMENT)
    private Long userSeq; // 기본 키

    @Column(nullable = false, unique = true) // NOT NULL, UNIQUE 제약 조건
    private String userId;

    @Column(nullable = false)
    private String userPw;

    @Column(nullable = false, unique = true)
    private String userEmail;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = true)
    private String socialType;

    @Column(nullable = true)
    private String socialPlatform;

    @Column(nullable = false, updatable = false) // 최초 생성 시 값 설정, 수정 불가
    private LocalDateTime createdAt;

    @Version
    @Column(nullable = false)
    private Long version;  // 추가된 버전 필드

    @PrePersist // 엔티티가 처음 저장되기 전에 실행
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
