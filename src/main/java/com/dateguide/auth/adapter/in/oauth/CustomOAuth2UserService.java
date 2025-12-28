package com.dateguide.auth.adapter.in.oauth;

import com.dateguide.auth.adapter.out.persistence.UserRepository;
import com.dateguide.auth.adapter.out.persistence.entity.UserEntity;
import com.dateguide.auth.domain.model.OAuthProvider;
import jakarta.transaction.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

   private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * OAuth 로그인 성공 후, provider userinfo를 가져온 뒤 이 메서드가 호출됨.
     * DB upsert + principal 구성
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.from(registrationId, oAuth2User.getAttributes());

        UserEntity user = upsertUser(userInfo);

        Map<String, Object> principalAttributes = new HashMap<>(oAuth2User.getAttributes());
        principalAttributes.put("userId", user.getId());
        principalAttributes.put("role", user.getRole().name());
        principalAttributes.put("provider", user.getProvider().name());

        Collection<? extends GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new DefaultOAuth2User(authorities, principalAttributes, "userId");

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
