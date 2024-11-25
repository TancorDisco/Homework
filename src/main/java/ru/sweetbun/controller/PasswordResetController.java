package ru.sweetbun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.DTO.password.PasswordChangeDTO;
import ru.sweetbun.DTO.password.PasswordResetRequestDTO;
import ru.sweetbun.service.PasswordResetService;

@RestController
@RequestMapping("/auth")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("req-pass-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequestDTO passwordDTO) {
        return ResponseEntity.ok(passwordResetService.requestPasswordReset(passwordDTO));
    }

    @PostMapping("reset-pass")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordChangeDTO passwordDTO) {
        return ResponseEntity.ok(passwordResetService.resetPassword(passwordDTO));
    }
}
