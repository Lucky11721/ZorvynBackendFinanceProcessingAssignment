package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.ServiceImpl;

import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.request.UserRegistrationRequestDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.response.UserResponseDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Entity.User;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Enum.Roles;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Exceptions.BadRequestException;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Exceptions.ResourceNotFoundException;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Repository.UserRepository;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // Make sure to import this
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Inject the password encoder

    @Override
    public UserResponseDTO registerUser(UserRegistrationRequestDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("User with this email already exists.");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // Encrypt the password here!
                .roles(Roles.valueOf(request.role().toUpperCase()))
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    @Override
    public UserResponseDTO updateUserStatus(Long userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String upperStatus = status.toUpperCase();
        if (!upperStatus.equals("ACTIVE") && !upperStatus.equals("INACTIVE")) {
            throw new BadRequestException("Invalid status. Use ACTIVE or INACTIVE.");
        }

        user.setStatus(upperStatus);
        return mapToDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        try {
            user.setRoles(Roles.valueOf(newRole.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Role: " + newRole);
        }

        return mapToDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private UserResponseDTO mapToDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getRoles().name()
        );
    }
}