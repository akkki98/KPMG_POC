package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.jwt.JwtService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Deprecated AuthController retained only as a placeholder after consolidating
 * auth endpoints into UserController to remove duplicate mappings. Contains no
 * request mappings and is not a @RestController anymore.
 */
@Deprecated
public class AuthController { }

