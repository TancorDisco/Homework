package ru.sweetbun.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sweetbun.service.TokenBlacklistService;
import ru.sweetbun.service.TokenService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Profile({"dev", "!test"})
@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public AuthorizationFilter(TokenService tokenService, TokenBlacklistService tokenBlacklistService) {
        this.tokenService = tokenService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/") || path.startsWith("/actuator") || path.startsWith("/prometheus");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String requestPath = request.getRequestURI();

        log.info("Processing request to {}", requestPath);

        if (header == null || header.isBlank()) {
            log.warn("Authorization header is missing or blank.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (!checkAuthorization(header)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String token = header.substring(7);
        if (tokenBlacklistService.isTokenBlacklisted(token)) {
            log.warn("Token is blacklisted.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = tokenService.getUsernameFromToken(token);
        List<GrantedAuthority> authorities = tokenService.getAuthoritiesFromToken(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username, null, authorities
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Token for user: {} with roles: {}", username, authorities);
        }

        filterChain.doFilter(request, response);
    }

    private boolean checkAuthorization(String auth) {
        if (!auth.startsWith("Bearer ")) return false;
        return tokenService.validateToken(auth.substring(7));
    }
}
