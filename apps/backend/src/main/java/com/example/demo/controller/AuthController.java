package com.example.demo.controller;

import com.example.demo.api.api.AuthApi;
import com.example.demo.api.model.LoginRequest;
import com.example.demo.api.model.LoginResponse;
import com.example.demo.api.model.UserProfile;
import com.example.demo.exception.SystemFault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@Validated
public class AuthController implements AuthApi {

    @Override
    public LoginResponse authLogin(LoginRequest request) {
        // Mock authentication
        if ("admin".equals(request.username()) && "admin".equals(request.password())) {
            UserProfile user = new UserProfile("1", "admin",
                    Arrays.asList("READ_TASKS", "DELETE_TASKS", "UPDATE_TASKS", "CREATE_TASKS"));
            return new LoginResponse("mock-admin-token", user);
        } else if ("user".equals(request.username()) && "user".equals(request.password())) {
            UserProfile user = new UserProfile("2", "user", Arrays.asList("READ_TASKS"));
            return new LoginResponse("mock-user-token", user);
        } else {
            throw new SystemFault("Invalid credentials", "UNAUTHORIZED", 401);
        }
    }
}
