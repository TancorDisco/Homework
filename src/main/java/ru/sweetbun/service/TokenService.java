package ru.sweetbun.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final long TOKEN_VALIDITY = 600_000;
    private final long TOKEN_REMEMBER_ME_VALIDITY = 2_592_000_000L;

    public String generateToken(String username, List<String> roles, boolean rememberMe) {
        log.info("Generating token for user: {} with roles: {}", username, roles);

        long validity = rememberMe ? TOKEN_REMEMBER_ME_VALIDITY : TOKEN_VALIDITY;
        List<String> prefixedRoles = roles.stream()
                .map(role -> "ROLE_" + role)
                .toList();

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + validity))
                .withClaim("roles", prefixedRoles)
                .sign(getAlgorithm());
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC512(jwtSecret);
    }

    private JWTVerifier getVerifier() {
        return JWT.require(getAlgorithm()).build();
    }

    public boolean validateToken(String token) {
        try {
            getVerifier().verify(token);
        } catch (JWTVerificationException e) {
            log.error("Token is invalid: " + e.getMessage());
            return false;
        }
        return true;
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = getVerifier().verify(token);
        return decodedJWT.getSubject();
    }

    public long getExpirationTimeInMinutes(String token) {
        DecodedJWT decodedJWT = getVerifier().verify(token);
        Date exporationDate = decodedJWT.getExpiresAt();
        return (exporationDate.getTime() - new Date().getTime()) / 60_000;
    }

    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        DecodedJWT decodedJWT = getVerifier().verify(token);
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
