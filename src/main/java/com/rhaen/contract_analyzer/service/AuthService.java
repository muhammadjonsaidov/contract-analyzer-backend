package com.rhaen.contract_analyzer.service;

import com.rhaen.contract_analyzer.dto.AuthDTOs;
import com.rhaen.contract_analyzer.entity.User;
import com.rhaen.contract_analyzer.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthDTOs.AuthResponse register(AuthDTOs.RegisterRequest request) {
        User user = new User();
        user.setFullName(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("USER");
        user.setProvider("LOCAL");
        
        User savedUser = repository.save(user);
        String jwtToken = jwtService.generateToken(savedUser);
        return new AuthDTOs.AuthResponse(jwtToken, savedUser.getId(), savedUser.getFullName(), savedUser.getEmail());
    }

    public AuthDTOs.AuthResponse authenticate(AuthDTOs.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = repository.findByEmail(request.email())
                .orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        return new AuthDTOs.AuthResponse(jwtToken, user.getId(), user.getFullName(), user.getEmail());
    }

    public AuthDTOs.AuthResponse handleOAuth2Success(String email, String name, String providerId) {
        User user = repository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(name);
            newUser.setProvider("GOOGLE");
            newUser.setProviderId(providerId);
            newUser.setRole("USER");
            return repository.save(newUser);
        });
        
        String jwtToken = jwtService.generateToken(user);
        return new AuthDTOs.AuthResponse(jwtToken, user.getId(), user.getFullName(), user.getEmail());
    }
}
