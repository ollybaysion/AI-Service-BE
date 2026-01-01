package com.dateguide.auth.adapter.in.me;

import com.dateguide.auth.adapter.out.persistence.UserRepository;
import com.dateguide.auth.adapter.out.persistence.entity.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/me")
public class MeController {

    private final UserRepository userRepository;

    public MeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public MeResponse me(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        Long userId;

        if (principal instanceof String s) {
            userId = Long.parseLong(s);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"
                ));

        return new MeResponse(userId, user.getEmail(), user.getName());
    }
}
