package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Controller;

import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.request.LoginRequestDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.response.AuthResponseDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Entity.User;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Exceptions.ResourceNotFoundException;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Repository.UserRepository;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Security.AuthUtil;
import io.swagger.v3.oas.annotations.Operation; // SWAGGER IMPORT
import io.swagger.v3.oas.annotations.tags.Tag;       // SWAGGER IMPORT
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Management", description = "Endpoints for logging in and securely acquiring JWT session tokens.") // SWAGGER ANNOTATION
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate User & Get Token",
            description = "Validates user credentials against the database. Upon success, it generates and returns a stateless JWT Bearer token required for accessing secure endpoints."
    ) // SWAGGER ANNOTATION
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {

        // 1. Verify the credentials against the database (Secure password check happens here automatically!)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // 2. If the code reaches this line, the password was correct. Let's fetch the user details.
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3. Generate the VIP Pass (JWT Token)
        String token = authUtil.generateAccessToken(user);

        // 4. Return the beautifully formatted response
        return ResponseEntity.ok(new AuthResponseDTO(
                token,
                user.getId(),
                user.getEmail(),
                user.getRoles().name()
        ));
    }
}