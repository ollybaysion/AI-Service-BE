package com.dateguide.auth.adapter.in.me;

import com.dateguide.auth.adapter.out.persistence.UserRepository;
import com.dateguide.auth.adapter.out.persistence.entity.UserEntity;
import com.dateguide.auth.domain.model.OAuthProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class MeControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void exists_user_return_response() {
        // given
        MeController controller = new MeController(userRepository);

        UserEntity user = new UserEntity(
                OAuthProvider.GOOGLE,
                "sub-123",
                "test@gmail.com",
                "Alice"
        );

        userRepository.save(user);
        Long userId = user.getId();

        var auth = new UsernamePasswordAuthenticationToken(userId, null, List.of());

        // when
        MeResponse res = controller.me(auth);

        // then
        assertThat(res.userId()).isEqualTo(userId);
        assertThat(res.email()).isEqualTo("test@gmail.com");
        assertThat(res.name()).isEqualTo("Alice");
    }
}