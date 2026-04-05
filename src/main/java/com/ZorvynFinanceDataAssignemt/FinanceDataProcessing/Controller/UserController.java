package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Controller;

import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.request.UserRegistrationRequestDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.response.UserResponseDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. Register a new user
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegistrationRequestDTO request) {
        return new ResponseEntity<>(userService.registerUser(request), HttpStatus.CREATED);
    }

    // 2. Get all users
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 3. Get user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // 4. Update user status (e.g., ?status=INACTIVE)
    @PatchMapping("/{userId}/status")
    public ResponseEntity<UserResponseDTO> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam String status) {
        return ResponseEntity.ok(userService.updateUserStatus(userId, status));
    }

    // 5. Update user role (e.g., ?role=ADMIN)
    @PatchMapping("/{userId}/role")
    public ResponseEntity<UserResponseDTO> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String role) {
        return ResponseEntity.ok(userService.updateUserRole(userId, role));
    }
}