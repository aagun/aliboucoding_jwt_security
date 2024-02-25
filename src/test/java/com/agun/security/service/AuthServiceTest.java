package com.agun.security.service;

import com.agun.security.dto.AuthenticationResponse;
import com.agun.security.dto.DefaultResponse;
import com.agun.security.dto.LoginRequest;
import com.agun.security.dto.RegisterRequest;
import com.agun.security.enums.Role;
import com.agun.security.model.User;
import com.agun.security.repository.UserRepository;
import com.agun.security.security.TokenManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.apache.tomcat.websocket.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Slf4j
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService underTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new AuthService(userRepository, passwordEncoder, tokenManager, authenticationManager);
    }

    @Test
    void itShouldRegisterUserIfNotExist() {
        // Given access token and register request
        final String accessToken = "access_token";

        // ... register request
        RegisterRequest request = new RegisterRequest("John", "Doe", "test@example.com", "plainPassword");

        // ... not existing user
        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password("hashedPassword")
                .role(Role.USER).build();

        // ... no exist user
        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);

        // ... hashed password
        given(passwordEncoder.encode(request.getPassword())).willReturn("hashedPassword");

        // ... generate access token
        given(tokenManager.generateToken(newUser)).willReturn("access_token");


        // When
        DefaultResponse<AuthenticationResponse> response = underTest.register(request);

        // Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(userArgumentCaptor.capture());
        User userArgumentCaptorValue = userArgumentCaptor.getValue();

        assertThat(userArgumentCaptorValue.getRole()).isEqualTo(Role.USER);
        assertThat(userArgumentCaptorValue.getPassword()).isEqualTo("hashedPassword");

        assertThat(response.getStatus()).isEqualTo("SUCCESSFUL");
        assertThat(response.getMessage()).isEqualTo("CREATED");
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getData().get(0).getToken()).isEqualTo(accessToken);

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void itShouldNotRegisterUserIfAlreadyExist() {
        // Given access token and register request
        final String accessToken = "access_token";

        // ... register request
        RegisterRequest request = new RegisterRequest("John", "Doe", "test@example.com", "plainPassword");
        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password("hashedPassword")
                .role(Role.USER).build();

        // ... no exist user
        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        // When
        assertThatThrownBy(() -> underTest.register(request)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already taken");

        // Then
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    void itShouldLoginSuccessfully() {
        // Given login request
        LoginRequest request = new LoginRequest("agun@mail.com", "plainPassword");

        // ... existing user
        User existingUser = mock(User.class);

        given(userRepository.findByEmail(any(String.class)))
                .willReturn(Optional.ofNullable(existingUser));

        // ... authentication manager
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(mock(Authentication.class));

        // ... generate access token
        given(tokenManager.generateToken(existingUser)).willReturn("access_token");

        // When
        DefaultResponse<AuthenticationResponse> result = underTest.login(request);

        // Then
        assertThat(result.getStatus()).isEqualTo("SUCCESSFUL");
        assertThat(result.getMessage()).isEqualTo("OK");
        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getToken()).isEqualTo("access_token");

    }

    @Test
    void itShouldLoginFailureForExistingUser() {
        // Given login request
        LoginRequest request = new LoginRequest("agun@mail.com", "plainPassword");

        // ... not existing user
        given(userRepository.findByEmail(any(String.class))).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.login(request))
                .isInstanceOf(IllegalArgumentException.class).
                hasMessage("Username or password incorrect");

    }

    @Test
    void itShouldLoginFailureForInvalidPassword() {
        // Given login request
        LoginRequest request = new LoginRequest("agun@mail.com", "plainPassword");

        // ... user
        User existingUser = User.builder()
                .firstName("agun")
                .lastName("agun")
                .email("agun@mail.com")
                .password("hashedPassword")
                .role(Role.USER).build();

        // ... existing user
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.ofNullable(existingUser));

        given(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())))
                .willThrow(new BadCredentialsException("Username or password incorrect"));

        // When
        // Then
        assertThatThrownBy(() -> underTest.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Username or password incorrect");

        // Verify that UserRepository method is called
        verify(userRepository, times(1)).findByEmail(request.getEmail());

        // Verify that AuthenticationManager method is called
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Verify that TokenManager method is NOT called in this case
        verify(tokenManager, never()).generateToken(existingUser);
    }
}