package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.User;
import com.example.demo.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final UserJpaRepository userRepository;
    @Value("${security.admin.emails:}")
    private String adminEmails;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegistrationDto dto) {
        return ResponseEntity.ok(userService.registerUser(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.authenticate(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        boolean isAdmin = false;
        if (adminEmails != null && !adminEmails.isBlank()) {
            for (String a : adminEmails.split(",")) {
                if (a.trim().equalsIgnoreCase(user.getEmail())) { isAdmin = true; break; }
            }
        }
        return ResponseEntity.ok(UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .roles(isAdmin ? "ROLE_USER,ROLE_ADMIN" : "ROLE_USER")
                .build());
    }

    @GetMapping("/users/exists")
    public ResponseEntity<Map<String,Object>> userExists(@RequestParam("email") String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email parameter is required");
        }
        boolean exists = userRepository.findByEmail(email).isPresent();
        return ResponseEntity.ok(Map.of(
                "email", email,
                "exists", exists
        ));
    }
}
