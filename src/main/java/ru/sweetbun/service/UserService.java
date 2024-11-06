package ru.sweetbun.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sweetbun.DTO.UserDTO;
import ru.sweetbun.entity.Role;
import ru.sweetbun.entity.User;
import ru.sweetbun.repository.UserRepository;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    private final TokenService tokenService;

    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, RoleService roleService, TokenService tokenService, TokenBlacklistService tokenBlacklistService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.roleService = roleService;
        this.tokenService = tokenService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public String register(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        String salt = generateSalt();
        user.setSalt(salt);
        user.setPassword(passwordEncoder.encode(user.getPassword() + salt));
        Role role = roleService.getRoleByName("USER");
        log.info("Default role of user: {}", role.getName());
        user.getRoles().add(role);
        userRepository.save(user);
        return user.getUsername();
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public String login(UserDTO userDTO, boolean rememberMe) {
        log.info("Attempting login for user: {}", userDTO.getUsername());

        List<SimpleGrantedAuthority> roles = authenticate(userDTO);

        if (roles.isEmpty()) {
            log.warn("No roles found for user: {}", userDTO.getUsername());
        }

        List<String> roleNames = roles.stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();
        return "Bearer " + tokenService.generateToken(userDTO.getUsername(), roleNames, rememberMe);
    }

    private List<SimpleGrantedAuthority> authenticate(UserDTO userDTO) {
        log.info("Authenticating user: {}", userDTO.getUsername());

        User user = getUserByUsername(userDTO.getUsername());
        String rawPasswordWithSalt = userDTO.getPassword() + user.getSalt();
        if (!passwordEncoder.matches(rawPasswordWithSalt, user.getPassword())) {
            log.error("Invalid username or password for user: {}", userDTO.getUsername());
            throw new UsernameNotFoundException("Invalid username or password");
        }
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> logout(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            long expInMinutes = tokenService.getExpirationTimeInMinutes(token);
            tokenBlacklistService.addTokenToBlacklist(token, expInMinutes);
            return ResponseEntity.ok("Logged out successfully");
        } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid authorization header");
    }

    public void updatePassword(User user, String password) {
        user.setPassword(password);
        userRepository.save(user);
    }
}
