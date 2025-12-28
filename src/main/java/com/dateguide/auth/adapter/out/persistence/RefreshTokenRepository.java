package com.dateguide.auth.adapter.out.persistence;

import com.dateguide.auth.adapter.out.persistence.entity.RefreshTokenEntity;
import com.dateguide.auth.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    /** refresh 검증 시 사용 */
    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    /** 유저의 모든 refresh 조회 */
    List<RefreshTokenEntity> findAllByUser(UserEntity user);

    /** 전체 기기 로그아웃 */
    long deleteByUser(UserEntity user);

    /** 만료 토큰 정리용 */
    long deleteByExpiresAtBefore(Instant now);
}
