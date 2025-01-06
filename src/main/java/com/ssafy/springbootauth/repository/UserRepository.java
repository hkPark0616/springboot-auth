package com.ssafy.springbootauth.repository;

import com.ssafy.springbootauth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 1. ID 및 Email 기반 유저 조회
    Optional<UserEntity> findByUserSeqAndEmail(String userSeq, String email);

    // 2. ID 기반 유저 조회
    Optional<UserEntity> findById(String id);

    // 3. 이름과 이메일 기반 ID 조회 (JPQL 사용)
    @Query("SELECT u.userId FROM UserEntity u WHERE u.userName = :name AND u.userEmail = :email")
    Optional<String> findIdByNameAndEmail(@Param("name") String name, @Param("email") String email);

    // 4. 유저 존재 여부 확인
    boolean existsByNameAndEmail(String name, String email);

    // 5. 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    // 6. 비밀번호 확인
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM UserEntity u WHERE u.userSeq = :userSeq AND u.userPw = :password")
    boolean checkPassword(@Param("userSeq") String userSeq, @Param("password") String password);

    // 7. 비밀번호 수정 (JPQL 사용)
    @Query("UPDATE UserEntity u SET u.userPw = :password WHERE u.userSeq = :userSeq")
    void updatePassword(@Param("userSeq") String userSeq, @Param("password") String newPassword);

    // 8. 유저 목록 페이징
    @Query("SELECT u FROM UserEntity u")
    List<UserEntity> findUserList(@Param("startIdx") int startIdx, @Param("size") int size);

    // 9. 특정 시퀀스 유저 조회
    Optional<UserEntity> findByUserSeq(String userSeq);

    // 10. 이메일 기반 시퀀스 조회
    @Query("SELECT u.userSeq FROM UserEntity u WHERE u.userEmail = :email")
    long findSeqByEmail(@Param("email") String email);

    // 11. 총 유저 수
    @Query("SELECT COUNT(u) FROM UserEntity u")
    int getTotalUsers();
}

