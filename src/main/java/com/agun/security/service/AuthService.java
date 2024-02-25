package com.agun.security.service;

import com.agun.security.dto.AuthenticationResponse;
import com.agun.security.dto.DefaultResponse;
import com.agun.security.dto.LoginRequest;
import com.agun.security.dto.RegisterRequest;
import com.agun.security.constant.Role;
import com.agun.security.model.User;
import com.agun.security.repository.UserRepository;
import com.agun.security.security.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.util.Collections.singletonList;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenManager tokenManager;

    private final AuthenticationManager authenticationManager;

    public DefaultResponse<AuthenticationResponse> register(RegisterRequest request) {

        // validate email
        if (this.userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Username already taken");
        }

        // hash the password
        final String hashedPassword = this.passwordEncoder.encode(request.getPassword());

        // store new user to database
        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(hashedPassword)
                .role(Role.USER)
                .build();

        this.userRepository.save(newUser);

        // generate access token
        final String token = tokenManager.generateToken(newUser);

        return DefaultResponse.<AuthenticationResponse>builder()
                .status(HttpStatus.Series.SUCCESSFUL.name())
                .message(HttpStatus.CREATED.name())
                .data(singletonList(new AuthenticationResponse(token)))
                .build();
    }

    public DefaultResponse<AuthenticationResponse> login(LoginRequest request) {

        User user = this.userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Username or password incorrect"));

        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        final String token = tokenManager.generateToken(user);

        return DefaultResponse.<AuthenticationResponse>builder()
                .status(HttpStatus.Series.SUCCESSFUL.name())
                .message(HttpStatus.OK.name())
                .data(singletonList(new AuthenticationResponse(token)))
                .build();
    }

}
