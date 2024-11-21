package ru.sweetbun.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTests {

    @InjectMocks
    private TokenService tokenService;

    private final String username = "testUser";
    private final List<String> roles = List.of("USER", "ADMIN");
    private final String secret = "mySecretKey";

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field secretField = TokenService.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(tokenService, secret);
    }

    @Test
    void generateToken_ValidInput_ReturnsToken() {
        String token = tokenService.generateToken(username, roles, false);
        assertNotNull(token);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = tokenService.generateToken(username, roles, false);
        assertTrue(tokenService.validateToken(token));
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalidToken";
        assertFalse(tokenService.validateToken(invalidToken));
    }

    @Test
    void getUsernameFromToken_ValidToken_ReturnsUsername() {
        String token = tokenService.generateToken(username, roles, false);
        String extractedUsername = tokenService.getUsernameFromToken(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void getUsernameFromToken_InvalidToken_ThrowsException() {
        String invalidToken = "invalidToken";
        assertThrows(JWTVerificationException.class, () -> tokenService.getUsernameFromToken(invalidToken));
    }

    @Test
    void getExpirationTimeInMinutes_ValidToken_ReturnsCorrectTime() {
        String token = tokenService.generateToken(username, roles, false);
        long expirationTime = tokenService.getExpirationTimeInMinutes(token);
        assertTrue(expirationTime > 0);
        assertTrue(expirationTime <= 10);
    }

    @Test
    void getExpirationTimeInMinutes_InvalidToken_ThrowsException() {
        String invalidToken = "invalidToken";
        assertThrows(JWTVerificationException.class, () -> tokenService.getExpirationTimeInMinutes(invalidToken));
    }

    @Test
    void getAuthoritiesFromToken_ValidToken_ReturnsAuthorities() {
        String token = tokenService.generateToken(username, roles, false);
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        assertEquals(authorities, tokenService.getAuthoritiesFromToken(token));
    }

    @Test
    void getAuthoritiesFromToken_InvalidToken_ThrowsException() {
        String invalidToken = "invalidToken";
        assertThrows(JWTVerificationException.class, () -> tokenService.getAuthoritiesFromToken(invalidToken));
    }
}
