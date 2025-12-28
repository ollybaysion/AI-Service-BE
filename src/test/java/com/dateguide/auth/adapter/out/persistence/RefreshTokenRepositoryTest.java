package com.dateguide.auth.adapter.out.persistence;

import com.dateguide.auth.adapter.out.persistence.entity.RefreshTokenEntity;
import com.dateguide.auth.adapter.out.persistence.entity.UserEntity;
import com.dateguide.auth.domain.model.OAuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class RefreshTokenRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("RefreshToken 저장 후 tokenHash 로 조회")
    void save_and_find_by_tokenHash() {
        // given
        UserEntity user = userRepository.save(
                new UserEntity(OAuthProvider.GOOGLE, "sub-rt-1", "a@gmail.com", "Alice")
        );

        Instant expiresAt = Instant.now().plus(14, ChronoUnit.DAYS);

        refreshTokenRepository.saveAndFlush(
                new RefreshTokenEntity(user, "hash-aaa", expiresAt)
        );

        // when
        RefreshTokenEntity found = refreshTokenRepository.findByTokenHash("hash-aaa")
                .orElseThrow();

        // then
        assertThat(found.getId()).isNotNull();
        assertThat(found.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(found.getRevokedAt()).isNull();

        assertThat(found.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("token_hash는 유니크 제약을 가진다")
    void token_hash_is_unique() {
       // given
        UserEntity user = userRepository.save(
                new UserEntity(OAuthProvider.GOOGLE, "sub-rt-2", "b@gmail.com", "Bob")
        );

        refreshTokenRepository.saveAndFlush(
                new RefreshTokenEntity(user, "hash-dup", Instant.now().plus(7, ChronoUnit.DAYS))
        );

        // when / then
        assertThatThrownBy(() ->
                refreshTokenRepository.saveAndFlush(
                        new RefreshTokenEntity(user, "hash-dup", Instant.now().plus(7, ChronoUnit.DAYS))
                )
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("한 유저는 여러 refresh token을 가질 수 있다")
    void user_can_have_multiple_refresh_tokens() {
        // given
        UserEntity user = userRepository.save(
                new UserEntity(OAuthProvider.NAVER, "sub-rt-3", null, "홍길동")
        );

        refreshTokenRepository.saveAndFlush(
                new RefreshTokenEntity(user, "hash-1", Instant.now().plus(30, ChronoUnit.DAYS))
        );
        refreshTokenRepository.saveAndFlush(
                new RefreshTokenEntity(user, "hash-2", Instant.now().plus(30, ChronoUnit.DAYS))
        );

        // when
        List<RefreshTokenEntity> tokens = refreshTokenRepository.findAllByUser(user);

        // then
        assertThat(tokens).hasSize(2);
        assertThat(tokens).extracting(RefreshTokenEntity::getTokenHash)
                .containsExactlyInAnyOrder("hash-1", "hash-2");
    }

    @Test
    @DisplayName("rotation: revokeAndReplaceWith 호출 시 revokedAt과 replacedByTokenHash가 저장된다")
    void revoke_and_rotation_should_persist() {
        // given
        UserEntity user = userRepository.save(
                new UserEntity(OAuthProvider.GOOGLE, "sub-rt-4", "c@gmail.com", "Chris")
        );

        RefreshTokenEntity oldToken = refreshTokenRepository.saveAndFlush(
                new RefreshTokenEntity(user, "hash-old", Instant.now().plus(30, ChronoUnit.DAYS))
        );

        Instant now = Instant.now();

        // when
        oldToken.revokeAndReplaceWith("hash-new", now);
        refreshTokenRepository.flush();

        // then
        RefreshTokenEntity found = refreshTokenRepository.findByTokenHash("hash-old")
                .orElseThrow();

        assertThat(found.isRevoked()).isTrue();
        assertThat(found.getRevokedAt()).isNotNull();
        assertThat(found.getReplacedByTokenHash()).isEqualTo("hash-new");
    }

    @Test
    @DisplayName("만료/폐기 판정 메서드")
    void expired_and_revoked_flags() {
        // given
        UserEntity user = userRepository.save(
                new UserEntity(OAuthProvider.GOOGLE, "sub-rt-5", "d@gmail.com", "Dana")
        );

        RefreshTokenEntity token = refreshTokenRepository.saveAndFlush(
                new RefreshTokenEntity(user, "hash-exp", Instant.now().minus(1, ChronoUnit.DAYS))
        );

        Instant now = Instant.now();

        // when / then
        assertThat(token.isExpired(now)).isTrue();
        assertThat(token.isRevoked()).isFalse();

        token.revoke(now);
        refreshTokenRepository.flush();

       RefreshTokenEntity found = refreshTokenRepository.findByTokenHash("hash-exp").orElseThrow();
       assertThat(found.isRevoked()).isTrue();
    }

    @Test
    @DisplayName("user 기준으로 refresh token을 일괄 삭제할 수 있다")
    void delete_by_user() {
        // given
        UserEntity user = userRepository.save(
                new UserEntity(OAuthProvider.NAVER, "sub-rt-6", null, "삭제테스트")
        );

        refreshTokenRepository.saveAndFlush(
                new RefreshTokenEntity(user, "hash-del-1", Instant.now().plus(10, ChronoUnit.DAYS))
        );

        refreshTokenRepository.saveAndFlush(
                new RefreshTokenEntity(user, "hash-del-2", Instant.now().plus(10, ChronoUnit.DAYS))
        );

        // when
        long deleted = refreshTokenRepository.deleteByUser(user);

        // then
        assertThat(deleted).isEqualTo(2);
        assertThat(refreshTokenRepository.findAllByUser(user)).isEmpty();
    }
}