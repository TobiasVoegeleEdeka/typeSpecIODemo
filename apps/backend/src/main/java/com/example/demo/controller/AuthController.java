package com.example.demo.controller;

import com.example.demo.api.model.LoginRequest;
import com.example.demo.api.model.LoginResponse;
import com.example.demo.api.model.UserProfile;
import com.example.demo.exception.SystemFault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // Mock authentication
        if ("admin".equals(request.username()) && "admin".equals(request.password())) {
            UserProfile user = new UserProfile("1", "admin", Arrays.asList("READ_TASKS", "DELETE_TASKS", "UPDATE_TASKS", "CREATE_TASKS"));
            return ResponseEntity.ok(new LoginResponse("mock-admin-token", user));
        } else if ("user".equals(request.username()) && "user".equals(request.password())) {
            UserProfile user = new UserProfile("2", "user", Arrays.asList("READ_TASKS"));
            return ResponseEntity.ok(new LoginResponse("mock-user-token", user));
        } else {
            throw new SystemFault("Invalid credentials", "UNAUTHORIZED", 401);
        }
    }
}
