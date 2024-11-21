package ru.sweetbun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.DTO.RoleDTO;
import ru.sweetbun.service.RoleService;
import ru.sweetbun.service.UserService;

@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    private final UserService userService;

    @Autowired
    public RoleController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(roleService.createRole(roleDTO));
    }

    @PatchMapping("/become-admin")
    public ResponseEntity<?> becomeAdmin() {
        return ResponseEntity.ok(userService.becomeAdmin());
    }
}
