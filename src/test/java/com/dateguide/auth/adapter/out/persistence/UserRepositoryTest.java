package com.dateguide.auth.adapter.out.persistence;

import com.dateguide.auth.adapter.out.persistence.entity.UserEntity;
import com.dateguide.auth.domain.model.OAuthProvider;
import com.dateguide.auth.domain.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("provider + providerUserId 조회")
    void save_and_find_by_provider_and_providerUserId() {
        // given
        UserEntity user = new UserEntity(
                OAuthProvider.GOOGLE,
                "sub-123",
                "test@gmail.com",
                "Alice"
        );

        userRepository.save(user);

        // when
        UserEntity found = userRepository
                .findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "sub-123")
                .orElseThrow();

        // then
        assertThat(found.getId()).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test@gmail.com");
        assertThat(found.getName()).isEqualTo("Alice");
        assertThat(found.getRole()).isEqualTo(UserRole.USER);
    }

   @Test
   @DisplayName("provider + providerUserId는 유니크 제약을 가진다")
   void unique_constraint_provider_and_providerUserId() {
       // given
       userRepository.saveAndFlush(
               new UserEntity(OAuthProvider.GOOGLE,
                       "dup-id",
                       "a@gmail.com",
                       "A")
       );

       // when / then
       assertThatThrownBy(() ->
               userRepository.saveAndFlush(
                       new UserEntity(OAuthProvider.GOOGLE,
                               "dup-id",
                               "a@gmail.com",
                               "A")
               )
       ).isInstanceOf(RuntimeException.class);
   }

   @Test
   @DisplayName("Profile Update")
   void update_profile() {
       // given
       UserEntity user = userRepository.save(
               new UserEntity(OAuthProvider.NAVER, "naver-1", null, "홍길동")
       );

       // when
       user.updateProfile("hong@naver.com", "홍길동2");
       userRepository.flush();

       // then
       UserEntity found = userRepository.findById(user.getId()).orElseThrow();
       assertThat(found.getEmail()).isEqualTo("hong@naver.com");
       assertThat(found.getName()).isEqualTo("홍길동2");
   }
}