package com.dateguide.auth.infra.jwt;

import com.dateguide.auth.domain.model.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider {

    private static final String CLAIM_ROLE = "role";

    private final JwtProperties props;
    private final SecretKey secretKey;

    public JwtProvider(JwtProperties props) {
        this.props = props;
        this.secretKey = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(long userId, UserRole role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.accessTtlSeconds());

        return Jwts.builder()
                .setIssuer(props.issuer())
                .setSubject(Long.toString(userId))
                .claim(CLAIM_ROLE, role.name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtClaims validateAndParse(String token) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .requireIssuer(props.issuer())
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            Claims c = jws.getBody();

            long userId = Long.parseLong(c.getSubject());
            String roleStr = c.get(CLAIM_ROLE, String.class);
            UserRole role = UserRole.valueOf(roleStr);

            Instant iat = c.getIssuedAt().toInstant();
            Instant exp = c.getExpiration().toInstant();

            return new JwtClaims(userId, role, iat, exp, c.getIssuer());
        } catch (ExpiredJwtException e) {
            throw new JwtException("Access token expired", e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Invalid access token", e);
        }
    }

}
