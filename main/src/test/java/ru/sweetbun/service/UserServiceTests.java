package ru.sweetbun.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sweetbun.DTO.UserDTO;
import ru.sweetbun.entity.Role;
import ru.sweetbun.entity.User;
import ru.sweetbun.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RoleService roleService;

    @Mock
    private TokenService tokenService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;
    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role(1L, "USER");
        userDTO = UserDTO.builder().username("test").password("test").build();
        user = User.builder().username("test").password("test").roles(new HashSet<>(Set.of(role))).salt("salt").build();

        lenient().when(modelMapper.map(userDTO, User.class)).thenReturn(user);
        lenient().when(roleService.getRoleByName("USER")).thenReturn(role);
    }

    @Test
    void register_ValidUserDTO_ReturnsUsername() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = userService.register(userDTO);

        assertEquals(userDTO.getUsername(), result);
        verify(userRepository).save(user);
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void register_UserAlreadyExists_ThrowsException() {
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> userService.register(userDTO));
    }

    @Test
    void login_ValidCredentials_ReturnsToken() {
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), eq(user.getPassword()))).thenReturn(true);
        when(tokenService.generateToken(anyString(), anyList(), eq(false))).thenReturn("validToken");

        String result = userService.login(userDTO, false);

        assertNotNull(result);
        assertTrue(result.startsWith("Bearer "));
        verify(tokenService).generateToken(userDTO.getUsername(), List.of("USER"), false);
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), eq(user.getPassword()))).thenReturn(false);

        assertThrows(UsernameNotFoundException.class, () -> userService.login(userDTO, false));
    }

    @Test
    void logout_ValidToken_ReturnsOkResponse() {
        String authHeader = "Bearer validToken";
        when(tokenService.getExpirationTimeInMinutes(anyString())).thenReturn(30L);

        ResponseEntity<?> response = userService.logout(authHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out successfully", response.getBody());
        verify(tokenBlacklistService).addTokenToBlacklist("validToken", 30L);
    }

    @Test
    void logout_InvalidTokenHeader_ReturnsBadRequest() {
        String invalidHeader = "InvalidHeader";

        ResponseEntity<?> response = userService.logout(invalidHeader);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid authorization header", response.getBody());
    }

    @Test
    void updatePassword_ValidUser_UpdatesPassword() {
        String newPassword = "newEncodedPassword";

        userService.updatePassword(user, newPassword);

        assertEquals(newPassword, user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void getUserByUsername_ExistingUser_ReturnsUser() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername(user.getUsername());

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    void getUserByUsername_NonExistingUser_ThrowsUsernameNotFoundException() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByUsername(user.getUsername()));
    }
}