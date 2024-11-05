package ru.sweetbun.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.testcontainers.shaded.org.bouncycastle.crypto.generators.BCrypt;
import ru.sweetbun.DTO.UserDTO;
import ru.sweetbun.entity.Role;
import ru.sweetbun.entity.User;
import ru.sweetbun.repository.UserRepository;

import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    private final TokenService tokenService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, RoleService roleService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.roleService = roleService;
        this.tokenService = tokenService;
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

    public Object login(UserDTO userDTO, boolean rememberMe) {
        authenticate(userDTO);
        String token = tokenService.generateToken(userDTO.getUsername(), rememberMe);
        return "Bearer " + token;
    }

    private void authenticate(UserDTO userDTO) {
        User user = getUserByUsername(userDTO.getUsername());
        String rawPasswordWithSalt = userDTO.getPassword() + user.getSalt();
        if (!passwordEncoder.matches(rawPasswordWithSalt, user.getPassword())) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }
}
