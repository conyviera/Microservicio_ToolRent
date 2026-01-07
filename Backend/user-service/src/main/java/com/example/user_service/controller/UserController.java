package com.example.user_service.controller;

import com.example.user_service.entity.UserEntity;
import com.example.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint de "Login" o "Perfil".
     * El Frontend llama aquí con el token.
     * El sistema valida el token, extrae datos y crea/actualiza el usuario en BD.
     */
    @GetMapping("/me")
    public ResponseEntity<UserEntity> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        // @AuthenticationPrincipal inyecta el JWT decodificado automáticamente
        UserEntity user = userService.syncUser(jwt);
        return ResponseEntity.ok(user);
    }
}