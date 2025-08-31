package com.example.demo.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private UUID id;
    private String name;
    private String email;
    private String status;
    private String roles; // comma-separated roles (e.g., ROLE_USER,ROLE_ADMIN)
}
