package com.securityagent.api_security_agent.controller;

import com.securityagent.api_security_agent.dto.AuthResponse;
import com.securityagent.api_security_agent.dto.LoginRequest;
import com.securityagent.api_security_agent.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // register new user
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody LoginRequest request) {
        String result = authService.register(
                request.getUsername(),
                request.getPassword());
        return ResponseEntity.ok(result);
    }

    // login and get JWT token
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {
        String token = authService.login(
                request.getUsername(),
                request.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }

}