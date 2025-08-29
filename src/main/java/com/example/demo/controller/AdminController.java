package com.example.demo.controller;

import com.example.demo.dto.UserResponseDto;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/pending")
    public ResponseEntity<List<UserResponseDto>> listPending() {
        return ResponseEntity.ok(userService.listPendingUsers());
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<UserResponseDto> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.approveUser(id));
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<UserResponseDto> reject(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.rejectUser(id));
    }
}
