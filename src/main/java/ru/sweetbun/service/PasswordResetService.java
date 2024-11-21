package ru.sweetbun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sweetbun.DTO.password.PasswordChangeDTO;
import ru.sweetbun.DTO.password.PasswordResetRequestDTO;
import ru.sweetbun.entity.User;

import java.util.HashMap;
import java.util.Map;

@Service
public class PasswordResetService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> otpStorage = new HashMap<>();

    @Autowired
    public PasswordResetService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public String requestPasswordReset(PasswordResetRequestDTO passwordDTO) {
        String username = passwordDTO.getUsername();
        userService.getUserByUsername(username);
        String otpCode = "0000";
        otpStorage.put(username, otpCode);
        return "Password reset code sent. Code: " + otpCode;
    }

    public String resetPassword(PasswordChangeDTO passwordDTO) {
        String username = passwordDTO.getUsername();
        verifyCode(username, passwordDTO.getCode());
        User user = userService.getUserByUsername(username);
        userService.updatePassword(user, passwordEncoder.encode(passwordDTO.getPassword() + user.getSalt()));
        otpStorage.remove(username);
        return "Password has been successfully reset.";
    }

    private void verifyCode(String username, String code) {
        String storedCode = otpStorage.get(username);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new IllegalArgumentException("Illegal verification code");
        }
    }
}
