package com.dateguide.auth.adapter.in.oauth;

import com.dateguide.auth.adapter.out.persistence.UserRepository;
import com.dateguide.auth.adapter.out.persistence.entity.UserEntity;
import com.dateguide.auth.domain.model.OAuthProvider;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomOidcUserService extends OidcUserService {

   private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * OAuth 로그인 성공 후, provider userinfo를 가져온 뒤 이 메서드가 호출됨.
     * DB upsert + principal 구성
     */
    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.from(registrationId, oidcUser.getAttributes());

        UserEntity user = upsertUser(userInfo);

        return oidcUser;

    }

    private UserEntity upsertUser(OAuth2UserInfo userInfo) {
        OAuthProvider provider = toProviderEnum(userInfo.provider());

        Optional<UserEntity> found = userRepository.findByProviderAndProviderUserId(
                provider, userInfo.providerUserId()
        );

        if (found.isPresent()) {
            UserEntity user = found.get();

            if (userInfo.email() != null) user.setEmail(userInfo.email());
            if (userInfo.name() != null) user.setName(userInfo.name());

            return user;
        }

        UserEntity newUser = new UserEntity(
                provider,
                userInfo.providerUserId(),
                userInfo.email(),
                userInfo.name()
        );

        return userRepository.save(newUser);
    }

    private OAuthProvider toProviderEnum(String provider) {
        return switch (provider.toLowerCase()) {
            case "google" -> OAuthProvider.GOOGLE;
            case "naver" -> OAuthProvider.NAVER;
            default -> throw new IllegalArgumentException("Unsupported provider");
        };
    }
}
