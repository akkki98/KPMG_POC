package com.example.demo.service;

import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UserService handles registration and approval workflow for {@link User} entities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // Messaging disabled temporarily
    // private final MessagingService messagingService;

    /**
     * Registers a new user with PENDING status and publishes a message to Service Bus.
     * @param dto registration payload
     * @return response DTO
     */
    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto dto) {
        userRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already registered");
        });

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .roleId(UUID.randomUUID()) // Placeholder role assignment.
                .status(User.Status.PENDING)
                .build();
        user = userRepository.save(user);

    // Messaging call disabled

        return toResponse(user);
    }

    /**
     * Lists users with PENDING status.
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto> listPendingUsers() {
    return userRepository.findByStatus(User.Status.PENDING).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Approves a user by id.
     */
    @Transactional
    public UserResponseDto approveUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getStatus() != User.Status.APPROVED) {
            user.setStatus(User.Status.APPROVED);
        }
        return toResponse(userRepository.save(user));
    }

    /**
     * Rejects a user by id.
     */
    @Transactional
    public UserResponseDto rejectUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getStatus() != User.Status.REJECTED) {
            user.setStatus(User.Status.REJECTED);
        }
        return toResponse(userRepository.save(user));
    }

    private UserResponseDto toResponse(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .build();
    }
}

