package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Service;

import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.request.UserRegistrationRequestDTO;
import com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.DTO.response.UserResponseDTO;
import java.util.List;

public interface UserService {
    // Core: Creating and managing users
    UserResponseDTO registerUser(UserRegistrationRequestDTO request);

    // Management: Status and Roles
    UserResponseDTO updateUserStatus(Long userId, String status);
    UserResponseDTO updateUserRole(Long userId, String newRole);

    // Viewing
    UserResponseDTO getUserById(Long userId);
    List<UserResponseDTO> getAllUsers();
}