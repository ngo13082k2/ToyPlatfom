package com.example.toyplatform_swp_project.services.implement;

import com.example.toyplatform_swp_project.dto.UserDTO;
import com.example.toyplatform_swp_project.model.User;
import com.example.toyplatform_swp_project.repository.UserRepository;
import com.example.toyplatform_swp_project.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    public UserDTO getUserById(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return mapToDTO(userOpt.get());
        }
        throw new RuntimeException("User not found");
    }

    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User existingUser = userOpt.get();
            User updatedUser = mapToEntity(userDTO, existingUser);
            userRepository.save(updatedUser);
            return mapToDTO(updatedUser);
        }
        throw new RuntimeException("User not found");
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setDob(user.getDob());
        dto.setAddress(user.getAddress());
        dto.setStatus(user.getStatus());
        dto.setRole(user.getRole());
        return dto;
    }

    private User mapToEntity(UserDTO dto, User existingUser) {
        if (dto.getFullName() != null && !dto.getFullName().isEmpty()) {
            existingUser.setFullName(dto.getFullName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            existingUser.setEmail(dto.getEmail());
        }

        if (dto.getDob() != null) {
            existingUser.setDob(dto.getDob());
        }

        if (dto.getAddress() != null && !dto.getAddress().isEmpty()) {
            existingUser.setAddress(dto.getAddress());
        }

        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            existingUser.setStatus(dto.getStatus());
        }

        if (dto.getRole() != null) {
            existingUser.setRole(dto.getRole());
        }

        existingUser.setModifiedDate(new Date());

        return existingUser;
    }

}
