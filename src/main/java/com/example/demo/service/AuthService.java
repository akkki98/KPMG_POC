package com.example.demo.service;

import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.jwt.JwtService;
import com.example.demo.model.User;
import com.example.demo.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Value("${security.admin.emails:}")
    private String adminEmails; // comma separated

    /**
     * Authenticates a user by email & password, generates JWT containing roles claim.
     * @throws IllegalArgumentException if credentials invalid or user not found.
     */
    public LoginResponseDto authenticate(LoginRequestDto dto) {
    log.debug("Authenticating email={}", dto.getEmail());
    User user = userRepository.findByEmail(dto.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            log.debug("Password mismatch for email={}", dto.getEmail());
            throw new IllegalArgumentException("Invalid credentials");
        }

    // Build roles inline (avoid second DB hit & potential UsernameNotFoundException path).
    List<String> configuredAdmins = Arrays.stream(adminEmails.split(","))
        .map(String::trim)
        .filter(s -> !s.isBlank())
        .toList();
    List<String> roles = new ArrayList<>();
    roles.add("ROLE_USER");
    boolean isAdmin = configuredAdmins.stream().anyMatch(a -> a.equalsIgnoreCase(user.getEmail()));
    if (isAdmin) roles.add("ROLE_ADMIN");
    String token = jwtService.generateToken(user.getEmail(), Map.of("roles", String.join(",", roles)));
    log.debug("Authentication success email={} roles={}", dto.getEmail(), roles);
        return LoginResponseDto.builder().token(token).build();
    }
}
