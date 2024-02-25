package com.agun.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
public class UserController {
    @GetMapping
    public ResponseEntity<String> getDataUser() {
        return ResponseEntity.ok()
                .body("Access Granted");
    }
}
