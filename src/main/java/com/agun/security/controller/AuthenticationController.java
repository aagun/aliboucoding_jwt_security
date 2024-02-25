package com.agun.security.controller;

import com.agun.security.dto.AuthenticationResponse;
import com.agun.security.dto.DefaultResponse;
import com.agun.security.dto.LoginRequest;
import com.agun.security.dto.RegisterRequest;
import com.agun.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<DefaultResponse<AuthenticationResponse>> register(@RequestBody RegisterRequest request) {
        DefaultResponse<AuthenticationResponse> response = this.authService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<DefaultResponse<AuthenticationResponse>> login(@RequestBody LoginRequest request) {
        DefaultResponse<AuthenticationResponse> response = this.authService.login(request);
        return ResponseEntity.status(200).body(response);
    }

}
