package com.rhaen.contract_analyzer.dto;

public class AuthDTOs {
    public record RegisterRequest(String username, String email, String password) {}
    public record LoginRequest(String email, String password) {}
    public record AuthResponse(String token, Long userId, String username, String email) {}
}
