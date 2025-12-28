package com.dateguide.auth.adapter.out.persistence;

import com.dateguide.auth.adapter.out.persistence.entity.UserEntity;
import com.dateguide.auth.domain.model.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);
}
