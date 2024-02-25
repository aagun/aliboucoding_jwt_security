package com.agun.security.controller;

import com.agun.security.dto.DefaultResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {
    @ExceptionHandler({
            AuthenticationException.class,
            BadCredentialsException.class,
            DisabledException.class,
            LockedException.class
    })
    public ResponseEntity<DefaultResponse<Object>> handleAuthenticationException(Exception exception) {
        DefaultResponse<Object> response = DefaultResponse.builder()
                .status(HttpStatus.Series.CLIENT_ERROR.name())
                .message(HttpStatus.Series.CLIENT_ERROR.name())
                .data(null)
                .build();

        if (AuthenticationController.class.isAssignableFrom(exception.getClass()) ||
                BadCredentialsException.class.isAssignableFrom(exception.getClass())) {
            response.setStatus(HttpStatus.FORBIDDEN.name());
            response.setMessage("Username or password incorrect");
            return ResponseEntity.status(403).body(response);
        }

        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<DefaultResponse<Object>> handleClientException(Exception exception) {
        DefaultResponse<Object> response = DefaultResponse.builder()
                .status(HttpStatus.Series.CLIENT_ERROR.name())
                .message(exception.getMessage())
                .data(null)
                .build();
        return ResponseEntity.badRequest().body(response);
    }
}
