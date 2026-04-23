package com.rhaen.contract_analyzer.controller;

import com.rhaen.contract_analyzer.dto.AuthDTOs;
import com.rhaen.contract_analyzer.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDTOs.AuthResponse> register(@RequestBody AuthDTOs.RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTOs.AuthResponse> login(@RequestBody AuthDTOs.LoginRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> oauth2Callback(@RequestParam String token) {
        return ResponseEntity.ok(Map.of("message", "OAuth2 login successful", "token", token));
    }
}
